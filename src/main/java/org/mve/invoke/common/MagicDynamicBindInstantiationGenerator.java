package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;
import org.mve.invoke.MethodKind;

public class MagicDynamicBindInstantiationGenerator extends DynamicBindInstantiationGenerator
{
	public MagicDynamicBindInstantiationGenerator(Class<?> target, MethodKind implementation)
	{
		super(target, implementation);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		MethodWriter mw = new MethodWriter()
			.set(AccessFlag.ACC_PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString())
			.addAttribute(new CodeWriter()
				.type(Opcodes.NEW, Generator.getType(getTarget()))
				.instruction(Opcodes.ARETURN)
				.max(1, 1 + Generator.parameterSize(implementation().type().parameterArray()))
			);
		bytecode.addMethod(mw);
		Generator.inline(mw);
	}
}
