package org.mve.asm.file;

public class AttributeSourceFile extends Attribute
{
	private short sourcefileIndex;

	public AttributeSourceFile(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getSourceFileIndex()
	{
		return sourcefileIndex;
	}

	public void setSourceFileIndex(short sourcefileIndex)
	{
		this.sourcefileIndex = sourcefileIndex;
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.SOURCE_FILE;
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
		b[index++] = (byte) ((this.sourcefileIndex >>> 8) & 0XFF);
		b[index] = (byte) (this.sourcefileIndex & 0XFF);
		return b;
	}
}
