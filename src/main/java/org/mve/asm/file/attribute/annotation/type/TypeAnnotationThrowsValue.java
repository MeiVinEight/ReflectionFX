package org.mve.asm.file.attribute.annotation.type;

public class TypeAnnotationThrowsValue extends TypeAnnotationValue
{
	public int thrown;

	@Override
	public int length()
	{
		return 2;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) ((this.thrown >>> 8) & 0XFF), (byte) (this.thrown & 0XFF)};
	}
}
