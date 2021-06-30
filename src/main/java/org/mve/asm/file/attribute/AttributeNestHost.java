package org.mve.asm.file.attribute;

public class AttributeNestHost extends Attribute
{
	public int host;

	@Override
	public AttributeType type()
	{
		return AttributeType.NEST_HOST;
	}

	@Override
	public int length()
	{
		return 8;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((name >>> 8) & 0XFF);
		b[index++] = (byte) (name & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) ((this.host >>> 8) & 0XFF);
		b[index] = (byte) (this.host & 0XFF);
		return b;
	}
}
