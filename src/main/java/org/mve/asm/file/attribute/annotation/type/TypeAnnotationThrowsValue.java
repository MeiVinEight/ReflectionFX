package org.mve.asm.file.attribute.annotation.type;

public class TypeAnnotationThrowsValue extends TypeAnnotationValue
{
	public int throwsType;

	@Override
	public int length()
	{
		return 2;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) ((this.throwsType >>> 8) & 0XFF), (byte) (this.throwsType & 0XFF)};
	}
}
