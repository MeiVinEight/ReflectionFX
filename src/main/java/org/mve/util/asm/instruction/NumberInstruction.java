package org.mve.util.asm.instruction;

public class NumberInstruction extends Instruction
{
	public final int num;

	public NumberInstruction(int opcode, int num)
	{
		super(opcode);
		this.num = num;
	}
}
