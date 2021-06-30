package org.mve.asm.file.attribute.annotation.type;

public class TypeAnnotationCatchValue extends TypeAnnotationValue
{
	public int exception;

	@Override
	public int length()
	{
		return 2;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) ((this.exception >>> 8) & 0XFF), (byte) (this.exception & 0XFF)};
	}
}
