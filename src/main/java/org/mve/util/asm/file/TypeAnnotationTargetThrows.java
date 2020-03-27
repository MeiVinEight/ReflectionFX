package org.mve.util.asm.file;

public class TypeAnnotationTargetThrows extends TypeAnnotationTarget
{
	private short throwsTypeIndex;

	public TypeAnnotationTargetThrows(byte targetType)
	{
		super(targetType);
	}

	public short getThrowsTypeIndex()
	{
		return throwsTypeIndex;
	}

	public void setThrowsTypeIndex(short throwsTypeIndex)
	{
		this.throwsTypeIndex = throwsTypeIndex;
	}

	@Override
	public int getLength()
	{
		return 2;
	}
}
