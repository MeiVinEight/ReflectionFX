package org.mve.asm.file;

public class ConstantInteger extends ConstantPoolElement
{
	private final int value;

	public ConstantInteger(int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_INTEGER;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[5];
		b[0] = this.getType().getCode();
		b[1] = (byte) ((this.value >>> 24) & 0XFF);
		b[2] = (byte) ((this.value >>> 16) & 0XFF);
		b[3] = (byte) ((this.value >>> 8) & 0XFF);
		b[4] = (byte) (this.value & 0XFF);
		return b;
	}
}
