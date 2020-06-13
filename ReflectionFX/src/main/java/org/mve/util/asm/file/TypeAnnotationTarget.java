package org.mve.util.asm.file;

import org.mve.util.Binary;

public abstract class TypeAnnotationTarget implements Binary
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
