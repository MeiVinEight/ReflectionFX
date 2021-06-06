package org.mve.asm.file;

public class AttributeSourceDebugExtension extends Attribute
{
	private byte[] extension = new byte[0];

	public AttributeSourceDebugExtension(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	@Override
	public AttributeType getType()
	{
		return null;
	}

	public byte[] getExtension()
	{
		return extension;
	}

	public void setExtension(byte[] extension)
	{
		this.extension = extension;
	}

	@Override
	public int getLength()
	{
		return this.extension.length+6;
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
		System.arraycopy(this.extension, 0, b, index, this.extension.length);
		return b;
	}
}
