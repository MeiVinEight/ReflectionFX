package org.mve.util.asm;

public class TypeAnnotationTargetEmpty extends TypeAnnotationTarget
{
	public TypeAnnotationTargetEmpty(byte targetType)
	{
		super(targetType);
	}

	@Override
	public int getLength()
	{
		return 0;
	}
}
