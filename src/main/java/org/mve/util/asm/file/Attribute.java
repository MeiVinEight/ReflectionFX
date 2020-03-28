package org.mve.util.asm.file;

import org.mve.util.Binary;

public abstract class Attribute implements Binary
{
	private final short attributeNameIndex;

	public Attribute(short attributeNameIndex)
	{
		this.attributeNameIndex = attributeNameIndex;
	}

	public abstract AttributeType getType();

	public short getAttributeNameIndex()
	{
		return attributeNameIndex;
	}

	public abstract int getLength();
}
