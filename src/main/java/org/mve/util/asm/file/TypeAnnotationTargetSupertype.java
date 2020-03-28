package org.mve.util.asm.file;

public class TypeAnnotationTargetSupertype extends TypeAnnotationTarget
{
	private short supertypeIndex;

	public TypeAnnotationTargetSupertype(byte targetType)
	{
		super(targetType);
	}

	public short getSupertypeIndex()
	{
		return supertypeIndex;
	}

	public void setSupertypeIndex(short supertypeIndex)
	{
		this.supertypeIndex = supertypeIndex;
	}

	@Override
	public int getLength()
	{
		return 2;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) ((this.supertypeIndex >>> 8) & 0XFF), (byte) (this.supertypeIndex & 0XFF)};
	}
}
