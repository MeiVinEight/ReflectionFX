package org.mve.util.asm.file;

public abstract class TypeAnnotationTarget
{
	private final byte targetType;

	public TypeAnnotationTarget(byte targetType)
	{
		this.targetType = targetType;
	}

	public abstract int getLength();

	public final byte getTargetType()
	{
		return targetType;
	}
}
