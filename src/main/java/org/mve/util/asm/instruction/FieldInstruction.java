package org.mve.util.asm.instruction;

public class FieldInstruction extends Instruction
{
	public final String type;
	public final String name;
	public final String desc;

	public FieldInstruction(int opcode, String type, String name, String desc)
	{
		super(opcode);
		this.type = type;
		this.name = name;
		this.desc = desc;
	}
}
