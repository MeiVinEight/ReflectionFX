package org.mve.util.asm;

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
		return 2;
	}
}
