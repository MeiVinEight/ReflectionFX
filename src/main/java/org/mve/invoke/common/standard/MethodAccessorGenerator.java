package org.mve.invoke.common.standard;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.MethodAccessor;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.JavaVM;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

public abstract class MethodAccessorGenerator extends AccessibleObjectAccessorGenerator
{
	private final Method method;
	private final int kind;
	private final int argument;

	public MethodAccessorGenerator(Method method, int kind, Object[] argument)
	{
		super(method, method.getDeclaringClass(), argument);
		this.method = method;
		this.kind = kind;
		this.argument = argument.length;
		this.bytecode().interfaces = new String[]{Generator.type(MethodAccessor.class)};
	}

	public void pregenerate(ClassWriter bytecode)
	{
		super.pregenerate(bytecode);
		bytecode.method(new MethodWriter()
			.set(AccessFlag.PUBLIC, MethodAccessor.METHOD, MethodType.methodType(Method.class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, bytecode.name, JavaVM.CONSTANT[ReflectionAccessor.FIELD_OBJECTIVE], Generator.signature(AccessibleObject.class))
				.type(Opcodes.CHECKCAST, Generator.type(Method.class))
				.instruction(Opcodes.ARETURN)
				.max(1, 1)
			)
		);
	}

	public Method getMethod()
	{
		return this.method;
	}

	public int kind()
	{
		return this.kind;
	}

	@Override
	public void generate()
	{
		this.bytecode()
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, ReflectionAccessor.WITH, MethodType.methodType(MethodAccessor.class, Object[].class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.consume(c -> Generator.merge(c, this.bytecode().name, argument))
					.field(Opcodes.GETSTATIC, this.bytecode().name, JavaVM.CONSTANT[ReflectionAccessor.FIELD_OBJECTIVE], Generator.signature(AccessibleObject.class))
					.type(Opcodes.CHECKCAST, Generator.type(Method.class))
					.number(Opcodes.BIPUSH, this.kind)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKESTATIC, Generator.type(Generator.class), ReflectionAccessor.METHOD_GENERATE, MethodType.methodType(MethodAccessor.class, Method.class, int.class, Object[].class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ARETURN)
					.max(5, 3)
				)
			);

		super.generate();
		Generator.with(this.bytecode(), MethodAccessor.class);
	}
}
