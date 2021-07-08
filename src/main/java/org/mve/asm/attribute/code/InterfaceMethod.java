package org.mve.asm.attribute.code;

public class InterfaceMethod extends Method
{
	public final int count;

	public InterfaceMethod(int opcode, String type, String name, String desc, int count)
	{
		super(opcode, type, name, desc, true);
		this.count = count;
	}
}
