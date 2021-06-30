package org.mve.asm.file.constant;

public class ConstantInteger extends Constant
{
	public int value;

	public ConstantInteger(int value)
	{
		this.value = value;
	}

	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_INTEGER;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[5];
		b[0] = this.type().code();
		b[1] = (byte) ((this.value >>> 24) & 0XFF);
		b[2] = (byte) ((this.value >>> 16) & 0XFF);
		b[3] = (byte) ((this.value >>> 8) & 0XFF);
		b[4] = (byte) (this.value & 0XFF);
		return b;
	}

	@Override
	public int length()
	{
		return 5;
	}
}
