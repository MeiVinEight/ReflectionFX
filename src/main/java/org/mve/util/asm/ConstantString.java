package org.mve.util.asm;

public class ConstantString extends ConstantPoolElement
{
	private final short stringIndex;

	public ConstantString(short stringIndex)
	{
		this.stringIndex = stringIndex;
	}

	public short getStringIndex()
	{
		return stringIndex;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_STRING;
	}
}
