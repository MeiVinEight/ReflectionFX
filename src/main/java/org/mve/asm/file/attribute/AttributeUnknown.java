package org.mve.asm.file.attribute;

public class AttributeUnknown extends Attribute
{
	public byte[] code = new byte[0];

	@Override
	public AttributeType type()
	{
		return AttributeType.UNKNOWN;
	}

	@Override
	public int length()
	{
		return this.code.length + 6;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = (byte) ((name >>> 8) & 0XFF);
		b[index++] = (byte) (name & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		System.arraycopy(this.code, 0, b, index, this.code.length);
		return b;
	}
}
