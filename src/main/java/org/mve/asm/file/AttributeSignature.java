package org.mve.asm.file;

public class AttributeSignature extends Attribute
{
	private short signatureIndex;

	public AttributeSignature(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getSignatureIndex()
	{
		return signatureIndex;
	}

	public void setSignatureIndex(short signatureIndex)
	{
		this.signatureIndex = signatureIndex;
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.SIGNATURE;
	}

	@Override
	public int getLength()
	{
		return 8;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[index++] = (byte) (this.getAttributeNameIndex() & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) ((this.signatureIndex >>> 8) & 0XFF);
		b[index] = (byte) (this.signatureIndex & 0XFF);
		return b;
	}
}
