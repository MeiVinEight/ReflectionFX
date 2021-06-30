package org.mve.asm.file.attribute.annotation.type;

public class TypeAnnotationTypeArgumentValue extends TypeAnnotationValue
{
	public int offset;
	public int argument;

	@Override
	public int length()
	{
		return 3;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) ((this.offset >>> 8) & 0XFF), (byte) (this.offset & 0XFF), (byte) this.argument};
	}
}
