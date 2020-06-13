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
		return 6;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[6];
		b[0] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[1] = (byte) (this.getAttributeNameIndex() & 0XFF);
		return b;
	}
}
