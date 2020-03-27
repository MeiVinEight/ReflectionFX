package org.mve.util.asm.file;

public class TypeAnnotationTargetTypeParameter extends TypeAnnotationTarget
{
	private byte typeParameterIndex;

	public TypeAnnotationTargetTypeParameter(byte targetType)
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

	@Override
	public int getLength()
	{
		return 1;
	}
}
