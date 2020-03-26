package org.mve.util.asm;

public class TypeAnnotationTargetFormalParameter extends TypeAnnotationTarget
{
	private byte formalParameterIndex;

	public TypeAnnotationTargetFormalParameter(byte targetType)
	{
		super(targetType);
	}

	public byte getFormalParameterIndex()
	{
		return formalParameterIndex;
	}

	public void setFormalParameterIndex(byte formalParameterIndex)
	{
		this.formalParameterIndex = formalParameterIndex;
	}

	@Override
	public int getLength()
	{
		return 1;
	}
}
