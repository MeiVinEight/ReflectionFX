package org.mve.asm.file.attribute.annotation.type;

public class TypeAnnotationEmptyValue extends TypeAnnotationValue
{
	@Override
	public int length()
	{
		return 0;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[0];
	}
}
