package org.mve.invoke.common.polymorphism;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.MethodKind;
import org.mve.invoke.common.Generator;

public class MagicPolymorphismInstantiationGenerator extends PolymorphismInstantiationGenerator
{
	public MagicPolymorphismInstantiationGenerator(Class<?> target, MethodKind implementation)
	{
		super(target, implementation);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		MethodWriter mw = new MethodWriter()
			.set(AccessFlag.PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString())
			.attribute(new CodeWriter()
				.type(Opcodes.NEW, Generator.type(getTarget()))
				.instruction(Opcodes.ARETURN)
				.max(1, 1 + Generator.parameterSize(implementation().type().parameterArray()))
			);
		bytecode.method(mw);
		Generator.inline(mw);
	}
}
