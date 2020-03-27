package org.mve.util.asm.file;

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
		return this.extension.length;
	}
}
