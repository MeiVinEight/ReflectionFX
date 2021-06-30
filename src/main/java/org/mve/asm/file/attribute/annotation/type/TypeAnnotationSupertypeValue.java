package org.mve.asm.file.attribute.annotation.type;

public class TypeAnnotationSupertypeValue extends TypeAnnotationValue
{
	public int supertype;

	@Override
	public int length()
	{
		return 2;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) ((this.supertype >>> 8) & 0XFF), (byte) (this.supertype & 0XFF)};
	}
}
