package org.mve.asm.file.constant;

public class ConstantUTF8 extends Constant
{
	public byte[] value;

	public ConstantUTF8(byte[] value)
	{
		this.value = value;
	}

	@Override
	public int length()
	{
		return this.value.length + 3;
	}

	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_UTF8;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.value.length + 3;
		byte[] b = new byte[len];
		b[0] = this.type().code();
		b[1] = (byte) ((this.value.length >>> 8) & 0XFF);
		b[2] = (byte) (this.value.length & 0XFF);
		System.arraycopy(this.value, 0, b, 3, this.value.length);
		return b;
	}
}
