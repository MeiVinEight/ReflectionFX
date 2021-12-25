package org.mve.invoke.common.standard;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.JavaVM;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class NativeMethodAccessorGenerator extends MethodAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Method method = this.getMethod();
	private final int argument;

	public NativeMethodAccessorGenerator(Method method, int kind, Object[] argument)
	{
		super(method, kind, argument);
		this.argument = argument.length;
	}

	@Override
	public void generate()
	{
		super.generate();
		int modifiers = this.method.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		this.bytecode.method(new MethodWriter()
			.set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.consume(c -> Generator.merge(c, this.bytecode.name, this.argument)) // 5
				.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
				.field(Opcodes.GETSTATIC, this.bytecode.name, JavaVM.CONSTANT[ReflectionAccessor.FIELD_OBJECTIVE], Generator.signature(AccessibleObject.class))
				.type(Opcodes.CHECKCAST, Generator.type(Method.class))
				.consume(c ->
				{
					if (statics)
					{
						c.field(Opcodes.GETSTATIC, this.bytecode.name, JavaVM.CONSTANT[ReflectionAccessor.FIELD_CLASS], Generator.signature(Class.class));
					}
					else
					{
						c.instruction(Opcodes.ALOAD_1)
							.instruction(Opcodes.ICONST_0)
							.instruction(Opcodes.AALOAD)
							.instruction(Opcodes.ALOAD_1)
							.instruction(Opcodes.ICONST_1)
							.instruction(Opcodes.ALOAD_1)
							.instruction(Opcodes.ARRAYLENGTH)
							.method(
								Opcodes.INVOKESTATIC,
								Generator.type(Arrays.class),
								"copyOfRange",
								MethodType
									.methodType(void.class, Object[].class, int.class, int.class)
									.toMethodDescriptorString(),
								false
							)
							.instruction(Opcodes.ASTORE_1);
					}
				})
				.instruction(Opcodes.ALOAD_1)
				.method(
					Opcodes.INVOKEINTERFACE,
					Generator.type(Unsafe.class),
					"invoke",
					MethodType
						.methodType(Object.class, Method.class, Object.class, Object[].class)
						.toMethodDescriptorString(),
					true
				)
				.instruction(Opcodes.ARETURN)
				.max(statics ? 5 : 6, 3)
			)
		);
	}
}
