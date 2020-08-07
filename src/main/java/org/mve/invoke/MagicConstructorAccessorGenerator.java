package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.file.AccessFlag;

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
		MethodWriter mw = this.bytecode.addMethod(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		Generator.inline(mw);
		CodeWriter code = mw.addCode();
		code.addTypeInstruction(Opcodes.NEW, Generator.getType(constructor.getDeclaringClass()))
			.addInstruction(Opcodes.DUP);
		Class<?>[] parameters = this.constructor.getParameterTypes();
		for (int i=0; i<parameters.length; i++)
		{
			code.addInstruction(Opcodes.ALOAD_1)
				.addNumberInstruction(Opcodes.BIPUSH, i)
				.addInstruction(Opcodes.AALOAD);
			if (parameters[i].isPrimitive())
			{
				Generator.unwarp(parameters[i], code);
			}
		}
		code.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(constructor.getDeclaringClass()), "<init>", MethodType.methodType(void.class, parameters).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(2 + (parameters.length == 0 ? 0 : parameters.length + 1), 2);
		if (parameters.length == 0)
		{
			mw = this.bytecode.addMethod(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString());
			Generator.inline(mw);
			mw.addCode()
				.addTypeInstruction(Opcodes.NEW, Generator.getType(this.constructor.getDeclaringClass()))
				.addInstruction(Opcodes.DUP)
				.addMethodInstruction(Opcodes.INVOKESPECIAL, Generator.getType(this.constructor.getDeclaringClass()), "<init>", "()V", false)
				.addInstruction(Opcodes.ARETURN)
				.setMaxs(2, 1);
		}
	}
}
