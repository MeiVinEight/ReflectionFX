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
}
