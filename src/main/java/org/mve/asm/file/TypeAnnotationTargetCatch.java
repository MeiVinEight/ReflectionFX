package org.mve.asm.file;

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

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) ((this.exceptionTableIndex >>> 8) & 0XFF), (byte) (this.exceptionTableIndex & 0XFF)};
	}
}
