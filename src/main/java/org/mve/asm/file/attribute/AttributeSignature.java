package org.mve.asm.file.attribute;

public class AttributeSignature extends Attribute
{
	public int signature;

	@Override
	public AttributeType type()
	{
		return AttributeType.SIGNATURE;
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
		b[index++] = (byte) ((this.signature >>> 8) & 0XFF);
		b[index] = (byte) (this.signature & 0XFF);
		return b;
	}
}
