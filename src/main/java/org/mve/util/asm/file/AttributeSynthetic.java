package org.mve.util.asm.file;

public class AttributeSynthetic extends Attribute
{
	public AttributeSynthetic(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.SYNTHETIC;
	}

	@Override
	public int getLength()
	{
		return 0;
	}
}
