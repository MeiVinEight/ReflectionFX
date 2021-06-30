package org.mve.asm.file.attribute.element;

public class ElementClassValue extends ElementValue
{
	public int clazz;

	@Override
	public int length()
	{
		return 3;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) type, (byte) ((this.clazz >>> 8) & 0XFF), (byte) (this.clazz & 0XFF)};
	}
}
