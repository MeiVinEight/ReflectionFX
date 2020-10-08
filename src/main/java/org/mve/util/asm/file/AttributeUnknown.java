package org.mve.util.asm.file;

public class AttributeUnknown extends Attribute
{
	public byte[] code = new byte[0];

	public AttributeUnknown(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.UNKNOWN;
	}

	@Override
	public int getLength()
	{
		return this.code.length + 6;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[index++] = (byte) (this.getAttributeNameIndex() & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		System.arraycopy(this.code, 0, b, index, this.code.length);
		return b;
	}
}
