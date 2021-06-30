package org.mve.asm.file.constant;

public class ConstantString extends Constant
{
	public int value;

	public ConstantString(int value)
	{
		this.value = value;
	}

	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_STRING;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[3];
		b[0] = this.type().code();
		b[1] = (byte) ((this.value >>> 8) & 0XFF);
		b[2] = (byte) (this.value & 0XFF);
		return b;
	}

	@Override
	public int length()
	{
		return 3;
	}
}
