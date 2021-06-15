package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;

public class MagicConstructorAccessorGenerator extends ConstructorAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Constructor<?> constructor;

	public MagicConstructorAccessorGenerator(Constructor<?> ctr)
	{
		super(ctr);
		this.constructor = ctr;
	}

	@Override
	public void generate()
	{
		MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		this.bytecode.addMethod(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.addAttribute(code);
		code.type(Opcodes.NEW, Generator.getType(constructor.getDeclaringClass()))
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
		code.method(Opcodes.INVOKESPECIAL, Generator.getType(constructor.getDeclaringClass()), "<init>", MethodType.methodType(void.class, parameters).toMethodDescriptorString(), false)
			.instruction(Opcodes.ARETURN)
			.max(2 + (parameters.length == 0 ? 0 : parameters.length + 1), 2);
		if (parameters.length == 0)
		{
			mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString())
				.addAttribute(new CodeWriter()
					.type(Opcodes.NEW, Generator.getType(this.constructor.getDeclaringClass()))
					.instruction(Opcodes.DUP)
					.method(Opcodes.INVOKESPECIAL, Generator.getType(this.constructor.getDeclaringClass()), "<init>", "()V", false)
					.instruction(Opcodes.ARETURN)
					.max(2, 1)
				);
			Generator.inline(mw);
			this.bytecode.addMethod(mw);
		}
	}
}
