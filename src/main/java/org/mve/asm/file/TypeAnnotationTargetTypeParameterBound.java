package org.mve.asm.file;

public class TypeAnnotationTargetTypeParameterBound extends TypeAnnotationTarget
{
	private byte typeParameterIndex;
	private byte boundIndex;

	public TypeAnnotationTargetTypeParameterBound(byte targetType)
	{
		super(targetType);
	}

	public byte getTypeParameterIndex()
	{
		return typeParameterIndex;
	}

	public void setTypeParameterIndex(byte typeParameterIndex)
	{
		this.typeParameterIndex = typeParameterIndex;
	}

	public byte getBoundIndex()
	{
		return boundIndex;
	}

	public void setBoundIndex(byte boundIndex)
	{
		this.boundIndex = boundIndex;
	}

	@Override
	public int getLength()
	{
		return 2;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{this.typeParameterIndex, this.boundIndex};
	}
}
