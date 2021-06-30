package org.mve.asm.file.attribute.element;

public class ElementConstantValue extends ElementValue
{
	public int value;

	@Override
	public int length()
	{
		return 3;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) type, (byte) ((this.value >>> 8) & 0XFF), (byte) (this.value & 0XFF)};
	}
}
