package org.mve.util.asm.instruction;

public abstract class Instruction
{
	private int opcode;

	public Instruction(int opcode)
	{
		this.opcode = opcode;
	}

	public int getOpcode()
	{
		return opcode;
	}

	public void setOpcode(int opcode)
	{
		this.opcode = opcode;
	}
}
