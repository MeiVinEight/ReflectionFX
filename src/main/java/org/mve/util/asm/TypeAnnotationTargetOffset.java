package org.mve.util.asm;

public class TypeAnnotationTargetOffset extends TypeAnnotationTarget
{
	private short offset;

	public TypeAnnotationTargetOffset(byte targetType)
	{
		super(targetType);
	}

	public short getOffset()
	{
		return offset;
	}

	public void setOffset(short offset)
	{
		this.offset = offset;
	}

	@Override
	public int getLength()
	{
		return 2;
	}
}
