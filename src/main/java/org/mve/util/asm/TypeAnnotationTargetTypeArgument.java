package org.mve.util.asm;

public class TypeAnnotationTargetTypeArgument extends TypeAnnotationTarget
{
	private byte offset;
	private short typeArgumentIndex;

	public TypeAnnotationTargetTypeArgument(byte targetType)
	{
		super(targetType);
	}

	public byte getOffset()
	{
		return offset;
	}

	public void setOffset(byte offset)
	{
		this.offset = offset;
	}

	public short getTypeArgumentIndex()
	{
		return typeArgumentIndex;
	}

	public void setTypeArgumentIndex(short typeArgumentIndex)
	{
		this.typeArgumentIndex = typeArgumentIndex;
	}

	@Override
	public int getLength()
	{
		return 3;
	}
}
