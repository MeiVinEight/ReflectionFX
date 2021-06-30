package org.mve.asm.file.attribute.annotation.type;

public class TypeAnnotationOffsetValue extends TypeAnnotationValue
{
	public int offset;

	@Override
	public int length()
	{
		return 2;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) ((this.offset >>> 8) & 0XFF), (byte) (this.offset & 0XFF)};
	}
}
