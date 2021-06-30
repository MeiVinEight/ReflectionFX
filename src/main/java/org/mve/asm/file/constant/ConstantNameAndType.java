package org.mve.asm.file.constant;

public class ConstantNameAndType extends Constant
{
	public int name;
	public int type;

	public ConstantNameAndType(int name, int type)
	{
		this.name = name;
		this.type = type;
	}

	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_NAME_AND_TYPE;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[5];
		b[0] = this.type().code();
		b[1] = (byte) ((this.name >>> 8) & 0XFF);
		b[2] = (byte) (this.name & 0XFF);
		b[3] = (byte) ((this.type >>> 8) & 0XFF);
		b[4] = (byte) (this.type & 0XFF);
		return b;
	}

	@Override
	public int length()
	{
		return 5;
	}
}
