package org.mve.util.asm.file;

public class ConstantInteger extends ConstantPoolElement
{
	private final int value;

	public ConstantInteger(int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_INTEGER;
	}
}
