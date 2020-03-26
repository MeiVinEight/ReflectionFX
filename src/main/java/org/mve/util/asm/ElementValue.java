package org.mve.util.asm;

public abstract class ElementValue
{
	private final byte type;

	public ElementValue(byte type)
	{
		this.type = type;
	}

	public byte getType()
	{
		return type;
	}

	public abstract int getLength();
}
