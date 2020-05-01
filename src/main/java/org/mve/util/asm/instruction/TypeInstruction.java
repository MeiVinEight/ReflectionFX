package org.mve.util.asm.instruction;

public class TypeInstruction extends Instruction
{
	public final String type;

	public TypeInstruction(int opcode, String type)
	{
		super(opcode);
		this.type = type;
	}
}
