package org.mve.asm.instruction;

import org.mve.asm.Opcodes;

public class ConstantInstruction extends Instruction
{
	public final Object value;

	public ConstantInstruction(Object value)
	{
		super(Opcodes.LDC);
		this.value = value;
	}
}
