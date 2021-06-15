package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;

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
				.type(Opcodes.NEW, Generator.getType(this.target))
				.instruction(Opcodes.ARETURN)
				.max(1, 2)
			);
		Generator.inline(mw);
		this.bytecode.addMethod(mw);

		mw = new MethodWriter()
		.set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString())
		.addAttribute(new CodeWriter()
			.type(Opcodes.NEW, Generator.getType(this.target))
			.instruction(Opcodes.ARETURN)
			.max(1, 1)
		);
		Generator.inline(mw);
		this.bytecode.addMethod(mw);
	}
}
