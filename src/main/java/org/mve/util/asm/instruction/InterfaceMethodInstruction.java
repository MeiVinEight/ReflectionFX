package org.mve.util.asm.instruction;

public class InterfaceMethodInstruction extends MethodInstruction
{
	public final int count;

	public InterfaceMethodInstruction(int opcode, String type, String name, String desc, int count)
	{
		super(opcode, type, name, desc, true);
		this.count = count;
	}
}
