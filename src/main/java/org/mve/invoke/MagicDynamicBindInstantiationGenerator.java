package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;
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
		MethodWriter mw = new MethodWriter()
			.set(AccessFlag.ACC_PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString())
			.addAttribute(new CodeWriter()
				.addTypeInstruction(Opcodes.NEW, Generator.getType(getTarget()))
				.addInstruction(Opcodes.ARETURN)
				.setMaxs(1, 1 + Generator.parameterSize(implementation().type().parameterArray()))
			);
		bytecode.addMethod(mw);
		Generator.inline(mw);
	}
}
