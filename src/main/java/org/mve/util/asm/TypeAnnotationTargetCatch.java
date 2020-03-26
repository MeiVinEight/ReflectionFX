package org.mve.util.asm;

public class TypeAnnotationTargetCatch extends TypeAnnotationTarget
{
	private short exceptionTableIndex;

	public TypeAnnotationTargetCatch(byte targetType)
	{
		super(targetType);
	}

	public short getExceptionTableIndex()
	{
		return exceptionTableIndex;
	}

	public void setExceptionTableIndex(short exceptionTableIndex)
	{
		this.exceptionTableIndex = exceptionTableIndex;
	}

	@Override
	public int getLength()
	{
		return 2;
	}
}
