package org.mve.util.asm.file;

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
