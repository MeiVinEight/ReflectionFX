package org.mve.asm.file;

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

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) ((this.offset >>> 8) & 0XFF), (byte) (this.offset & 0XFF)};
	}
}
