package org.mve.util.asm.instruction;

public class MethodInstruction extends Instruction
{
	public final String type;
	public final String name;
	public final String desc;
	public final boolean isAbstract;

	public MethodInstruction(int opcode, String type, String name, String desc, boolean isAbstract)
	{
		super(opcode);
		this.type = type;
		this.name = name;
		this.desc = desc;
		this.isAbstract = isAbstract;
	}
}
