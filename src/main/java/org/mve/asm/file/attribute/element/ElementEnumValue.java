package org.mve.asm.file.attribute.element;

public class ElementEnumValue extends ElementValue
{
	public int name;
	public int value;

	@Override
	public int length()
	{
		return 5;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]
		{
			(byte) type,
			(byte) ((this.name >>> 8) & 0XFF),
			(byte) (this.name & 0XFF),
			(byte) ((this.value >>> 8) & 0XFF),
			(byte) (this.value & 0XFF)
		};
	}
}
