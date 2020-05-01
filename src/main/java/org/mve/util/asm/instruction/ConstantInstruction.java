package org.mve.util.asm.instruction;

public class ConstantInstruction extends Instruction
{
	public final Object value;

	public ConstantInstruction(int opcode, Object value)
	{
		super(opcode);
		this.value = value;
	}
}
