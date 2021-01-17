package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;
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
		MethodWriter mw = new MethodWriter()
			.set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString())
			.addAttribute(new CodeWriter()
				.addTypeInstruction(Opcodes.NEW, Generator.getType(this.target))
				.addInstruction(Opcodes.ARETURN)
				.setMaxs(1, 2)
			);
		Generator.inline(mw);
		this.bytecode.addMethod(mw);

		mw = new MethodWriter()
		.set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString())
		.addAttribute(new CodeWriter()
			.addTypeInstruction(Opcodes.NEW, Generator.getType(this.target))
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 1)
		);
		Generator.inline(mw);
		this.bytecode.addMethod(mw);
	}
}
