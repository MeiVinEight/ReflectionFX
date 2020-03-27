package org.mve.util.asm.file;

public class AttributeDeprecated extends Attribute
{
	public AttributeDeprecated(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.DEPRECATED;
	}

	@Override
	public int getLength()
	{
		return 0;
	}
}
