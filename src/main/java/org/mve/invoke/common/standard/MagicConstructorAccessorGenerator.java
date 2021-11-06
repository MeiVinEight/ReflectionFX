package org.mve.invoke.common.standard;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.common.Generator;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;

public class MagicConstructorAccessorGenerator extends ConstructorAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Constructor<?> constructor;
	private final int argument;

	public MagicConstructorAccessorGenerator(Constructor<?> ctr, Object[] argument)
	{
		super(ctr, argument);
		this.constructor = ctr;
		this.argument = argument.length;
	}

	@Override
	public void generate()
	{
		super.generate();
		MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		this.bytecode.method(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.attribute(code);
		Generator.merge(code, this.bytecode.name, this.argument);
		code.type(Opcodes.NEW, Generator.type(constructor.getDeclaringClass()))
			.instruction(Opcodes.DUP);
		Class<?>[] parameters = this.constructor.getParameterTypes();
		for (int i=0; i<parameters.length; i++)
		{
			code.instruction(Opcodes.ALOAD_1)
				.number(Opcodes.BIPUSH, i)
				.instruction(Opcodes.AALOAD);
			if (parameters[i].isPrimitive())
			{
				Generator.unwarp(parameters[i], code);
			}
		}
		code.method(Opcodes.INVOKESPECIAL, Generator.type(constructor.getDeclaringClass()), "<init>", MethodType.methodType(void.class, parameters).toMethodDescriptorString(), false)
			.instruction(Opcodes.ARETURN)
			.max(2 + (parameters.length == 0 ? 0 : parameters.length + 1), 3);
	}
}
