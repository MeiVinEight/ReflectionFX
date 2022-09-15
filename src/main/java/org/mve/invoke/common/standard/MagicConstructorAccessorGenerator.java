package org.mve.invoke.common.standard;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ModuleAccess;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.JavaVM;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class MagicConstructorAccessorGenerator extends ConstructorAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Constructor<?> constructor;
	private final int argument;
	private final Object[] access = new Object[2];

	public MagicConstructorAccessorGenerator(Constructor<?> ctr, Object[] argument)
	{
		super(ctr, argument);
		this.constructor = ctr;
		this.argument = argument.length;
		this.access[0] = JavaVM.random();
	}

	@Override
	public void generate()
	{
		super.generate();

		String name = JavaVM.random();
		Class<?>[] parameters = this.constructor.getParameterTypes();

		ClassWriter abstractAccess = new ClassWriter()
			.set(
				Opcodes.version(8),
				AccessFlag.PUBLIC | AccessFlag.ABSTRACT | AccessFlag.INTERFACE,
				JavaVM.random(),
				Generator.type(Object.class)
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, name, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString())
			);
		ClassWriter access = new ClassWriter()
			.set(
				Opcodes.version(8),
				AccessFlag.PUBLIC | AccessFlag.SUPER,
				JavaVM.randomAnonymous(this.constructor.getDeclaringClass()),
				JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC],
				abstractAccess.name
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, name, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.type(Opcodes.NEW, Generator.type(this.constructor.getDeclaringClass()))
					.instruction(Opcodes.DUP)
					.consume(c ->
					{
						for (int i=0; i<parameters.length; i++)
						{
							c.instruction(Opcodes.ALOAD_1)
								.number(Opcodes.BIPUSH, i)
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
						Opcodes.INVOKESPECIAL,
						Generator.type(constructor.getDeclaringClass()),
						"<init>",
						MethodType.methodType(void.class, parameters).toMethodDescriptorString(),
						false
					)
					.instruction(Opcodes.ARETURN)
					.max(2 + (parameters.length == 0 ? 0 : parameters.length + 1), 3)
				)
				.consume(Generator::inline)
			);

		{
			byte[] code = abstractAccess.toByteArray();
			Class<?> intf = Generator.UNSAFE.defineClass(null, code, 0, code.length, null, null);
			ModuleAccess.read(ModuleAccess.module(this.constructor.getDeclaringClass()), ModuleAccess.module(intf));
			Class<?> c = Generator.UNSAFE.defineAnonymousClass(this.constructor.getDeclaringClass(), access.toByteArray(), null);
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

	@Override
	public void postgenerate(Class<?> generated)
	{
		super.postgenerate(generated);
		Field field = ReflectionFactory.ACCESSOR.getField(generated, (String) this.access[0]);
		long offset = Generator.UNSAFE.staticFieldOffset(field);
		Generator.UNSAFE.putObject(generated, offset, this.access[1]);
	}
}
