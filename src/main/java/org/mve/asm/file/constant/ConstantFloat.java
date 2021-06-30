package org.mve.asm.file.constant;

public class ConstantFloat extends Constant
{
	public float value;

	public ConstantFloat(float value)
	{
		this.value = value;
	}

	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_FLOAT;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[5];
		int i =  Float.floatToIntBits(this.value);
		b[0] = this.type().code();
		b[1] = (byte) ((i >>> 24) & 0XFF);
		b[2] = (byte) ((i >>> 16) & 0XFF);
		b[3] = (byte) ((i >>> 8) & 0XFF);
		b[4] = (byte) (i & 0XFF);
		return b;
	}

	@Override
	public int length()
	{
		return 5;
	}
}
