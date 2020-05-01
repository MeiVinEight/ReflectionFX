package org.mve.util.asm.instruction;

public abstract class Instruction
{
	public final int opcode;

	public Instruction(int opcode)
	{
		this.opcode = opcode;
	}
}
