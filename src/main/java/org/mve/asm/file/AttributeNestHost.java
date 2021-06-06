package org.mve.asm.file;

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
		b[index++] = (byte) ((this.hostClassIndex >>> 8) & 0XFF);
		b[index] = (byte) (this.hostClassIndex & 0XFF);
		return b;
	}
}
