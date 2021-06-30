package org.mve.asm.file.attribute;

public class AttributeDeprecated extends Attribute
{
	@Override
	public AttributeType type()
	{
		return AttributeType.DEPRECATED;
	}

	@Override
	public int length()
	{
		return 6;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[6];
		b[0] = (byte) ((name >>> 8) & 0XFF);
		b[1] = (byte) (name & 0XFF);
		return b;
	}
}
