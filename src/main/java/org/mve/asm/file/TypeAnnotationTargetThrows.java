package org.mve.asm.file;

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

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) ((this.throwsTypeIndex >>> 8) & 0XFF), (byte) (this.throwsTypeIndex & 0XFF)};
	}
}
