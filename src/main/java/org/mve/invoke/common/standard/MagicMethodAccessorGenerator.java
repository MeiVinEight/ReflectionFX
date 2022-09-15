package org.mve.invoke.common.standard;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.OperandStack;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ModuleAccess;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.JavaVM;

import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MagicMethodAccessorGenerator extends MethodAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Method method = this.getMethod();
	private final int kind = this.kind();
	private final int argument;
	private final Object[] access = new Object[2];

	public MagicMethodAccessorGenerator(Method method, int kind, Object[] argument)
	{
		super(method, kind, argument);
		this.argument = argument.length;
		this.access[0] = JavaVM.random();
	}

	@Override
	public void generate()
	{
		super.generate();
		int modifiers = this.method.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		boolean	interfaces = Modifier.isAbstract(modifiers);
		Class<?>[] parameters = this.method.getParameterTypes();

		String name = JavaVM.random();

		ClassWriter abstractAccess = new ClassWriter()
			.set(
				Opcodes.version(8),
				AccessFlag.PUBLIC | AccessFlag.ABSTRACT | AccessFlag.INTERFACE,
				JavaVM.random(),
				Generator.type(Object.class),
				null
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, name, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString())
			);

		ClassWriter access = new ClassWriter()
			.set(
				Opcodes.version(8),
				AccessFlag.PUBLIC | AccessFlag.SUPER,
				JavaVM.randomAnonymous(this.method.getDeclaringClass()),
				JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC],
				abstractAccess.name
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, name, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.consume(c ->
					{
						if (!statics)
						{
							c.instruction(Opcodes.ALOAD_1)
								.instruction(Opcodes.ICONST_0)
								.instruction(Opcodes.AALOAD)
								.type(Opcodes.CHECKCAST, Generator.type(this.method.getDeclaringClass()));
						}
					})
					.consume(c ->
					{
						for (int i = 0; i < parameters.length; i++)
						{
							c.instruction(Opcodes.ALOAD_1)
								.number(Opcodes.BIPUSH, i + (statics ? 0 : 1))
								.instruction(Opcodes.AALOAD);
							if (parameters[i].isPrimitive())
							{
								Generator.unwarp(parameters[i], c);
							}
							else
							{
								c.type(Opcodes.CHECKCAST, Generator.type(parameters[i]));
							}
						}
					})
					.method(
						this.kind + 0xB6,
						Generator.type(this.method.getDeclaringClass()),
						this.method.getName(),
						MethodType.methodType(this.method.getReturnType(), parameters).toMethodDescriptorString(),
						interfaces
					)
					.consume(c ->
					{
						if (method.getReturnType() == void.class)
						{
							c.instruction(Opcodes.ACONST_NULL);
						}
						else
						{
							Generator.warp(method.getReturnType(), c);
						}
					})
					.instruction(Opcodes.ARETURN)
					.max(this.stack(), 3)
				)
				.consume(Generator::inline)
			);

		{
			byte[] code = abstractAccess.toByteArray();
			Class<?> intf = Generator.UNSAFE.defineClass(null, code, 0, code.length, null, null);
			ModuleAccess.read(ModuleAccess.module(this.method.getDeclaringClass()), ModuleAccess.module(intf));
			Class<?> c = Generator.UNSAFE.defineAnonymousClass(this.method.getDeclaringClass(), access.toByteArray(), null);
			this.access[1] = Generator.UNSAFE.allocateInstance(c);
		}

		this.bytecode
			.field(new FieldWriter()
				.set(
					AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL,
					(String) this.access[0],
					"L" + abstractAccess.name + ";"
				)
			)
			.method(new MethodWriter()
			.set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.consume(c -> Generator.merge(c, this.bytecode.name, this.argument))
				.field(Opcodes.GETSTATIC, this.bytecode.name, (String) this.access[0], "L" + abstractAccess.name + ";")
				.instruction(Opcodes.ALOAD_1)
				.method(
					Opcodes.INVOKEINTERFACE,
					abstractAccess.name,
					name,
					MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(),
					true
				)
				.instruction(Opcodes.ARETURN)
				.max(5, 3)
			)
			.consume(Generator::inline)
		);
	}

	private int stack()
	{
		OperandStack stack = new OperandStack();
		int modifiers = this.method.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		if (!statics)
		{
			stack.push();
		}
		Class<?>[] parameters = this.method.getParameterTypes();
		for (Class<?> c : parameters)
		{
			stack.push();
			if (c == double.class || c == long.class)
			{
				stack.push();
			}
		}
		for (Class<?> c : parameters)
		{
			stack.pop();
			if (c == double.class || c == long.class)
			{
				stack.pop();
			}
		}
		if (!statics)
		{
			stack.pop();
		}
		stack.push();
		if (this.method.getReturnType() == long.class || this.method.getReturnType() == double.class)
		{
			stack.push();
		}
		stack.pop();
		if (this.method.getReturnType() == long.class || this.method.getReturnType() == double.class)
		{
			stack.pop();
		}
		return stack.getMaxSize();
	}

	@Override
	public void postgenerate(Class<?> generated)
	{
		super.postgenerate(generated);
		Field field = ReflectionFactory.ACCESSOR.getField(generated, (String) this.access[0]);
		long offset = Generator.UNSAFE.staticFieldOffset(field);
		Generator.UNSAFE.putObject(generated, offset, this.access[1]);
	}
}
