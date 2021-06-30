package org.mve.asm.file.attribute.annotation.type;

public abstract class TypeAnnotationValue
{
	public int type;

	public int type()
	{
		return type;
	}

	public abstract int length();

	public abstract byte[] toByteArray();
}
