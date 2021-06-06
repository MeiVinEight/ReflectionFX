package org.mve.asm.instruction;

public class LocalVariableInstruction extends Instruction
{
	public final int index;

	public LocalVariableInstruction(int opcode, int index)
	{
		super(opcode);
		this.index = index;
	}
}
