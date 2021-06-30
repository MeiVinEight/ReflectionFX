package org.mve.asm.file.attribute;

public class AttributeSourceFile extends Attribute
{
	public int source;

	@Override
	public AttributeType type()
	{
		return AttributeType.SOURCE_FILE;
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
		b[index++] = (byte) ((this.source >>> 8) & 0XFF);
		b[index] = (byte) (this.source & 0XFF);
		return b;
	}
}
