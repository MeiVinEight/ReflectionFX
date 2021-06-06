package org.mve.asm.instruction;

import org.mve.asm.Opcodes;

public class IincInstruction extends Instruction
{
	public final int indexbyte;
	public final int constbyte;

	public IincInstruction(int indexbyte, int constbyte)
	{
		super(Opcodes.IINC);
		this.indexbyte = indexbyte;
		this.constbyte = constbyte;
	}
}
