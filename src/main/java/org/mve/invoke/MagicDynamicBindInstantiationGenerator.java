package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.file.AccessFlag;

public class MagicDynamicBindInstantiationGenerator extends DynamicBindInstantiationGenerator
{
	public MagicDynamicBindInstantiationGenerator(Class<?> target, MethodKind implementation)
	{
		super(target, implementation);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		MethodWriter mw = bytecode.addMethod(AccessFlag.ACC_PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString());
		Generator.inline(mw);
		mw.addCode()
			.addTypeInstruction(Opcodes.NEW, Generator.getType(getTarget()))
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 1 + Generator.parameterSize(implementation().type().parameterArray()));
	}
}
