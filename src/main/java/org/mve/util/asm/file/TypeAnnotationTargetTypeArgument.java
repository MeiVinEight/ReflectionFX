package org.mve.util.asm.file;

public class TypeAnnotationTargetTypeArgument extends TypeAnnotationTarget
{
	private short offset;
	private byte typeArgumentIndex;

	public TypeAnnotationTargetTypeArgument(byte targetType)
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

	public byte getTypeArgumentIndex()
	{
		return typeArgumentIndex;
	}

	public void setTypeArgumentIndex(byte typeArgumentIndex)
	{
		this.typeArgumentIndex = typeArgumentIndex;
	}

	@Override
	public int getLength()
	{
		return 3;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) ((this.offset >>> 8) & 0XFF), (byte) (this.offset & 0XFF), this.typeArgumentIndex};
	}
}
