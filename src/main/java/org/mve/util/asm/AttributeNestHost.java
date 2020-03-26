package org.mve.util.asm;

public class AttributeNestHost extends Attribute
{
	private short hostClassIndex;

	public AttributeNestHost(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getHostClassIndex()
	{
		return hostClassIndex;
	}

	public void setHostClassIndex(short hostClassIndex)
	{
		this.hostClassIndex = hostClassIndex;
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.NEST_HOST;
	}

	@Override
	public int getLength()
	{
		return 2;
	}
}
