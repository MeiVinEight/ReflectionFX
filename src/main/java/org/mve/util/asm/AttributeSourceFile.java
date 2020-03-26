package org.mve.util.asm;

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
		return 2;
	}
}
