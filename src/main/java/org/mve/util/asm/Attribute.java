package org.mve.util.asm;

public abstract class Attribute
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
