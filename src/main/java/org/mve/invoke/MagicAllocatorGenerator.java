package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.file.AccessFlag;

import java.lang.invoke.MethodType;

public class MagicAllocatorGenerator extends AllocatorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Class<?> target;

	public MagicAllocatorGenerator(Class<?> target)
	{
		super(target);
		this.target = target;
	}

	@Override
	public void generate()
	{
		MethodWriter mw = this.bytecode.addMethod(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		Generator.inline(mw);
		mw.addCode()
			.addTypeInstruction(Opcodes.NEW, Generator.getType(this.target))
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 2);
		mw = this.bytecode.addMethod(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString());
		Generator.inline(mw);
		mw.addCode()
			.addTypeInstruction(Opcodes.NEW, Generator.getType(this.target))
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 1);
	}
}
