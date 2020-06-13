package org.mve.util.asm.file;

import org.mve.util.Binary;

public abstract class ElementValue implements Binary
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
