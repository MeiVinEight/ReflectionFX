package org.mve.asm.file.constant;

public class ConstantMethodType extends Constant
{
	public int type;

	public ConstantMethodType(int type)
	{
		this.type = type;
	}

	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_METHOD_TYPE;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[3];
		b[0] = this.type().code();
		b[1] = (byte) ((this.type >>> 8) & 0XFF);
		b[2] = (byte) (this.type & 0XFF);
		return b;
	}

	@Override
	public int length()
	{
		return 3;
	}
}
