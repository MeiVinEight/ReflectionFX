package org.mve.asm.file.constant;

public class ConstantModule extends Constant
{
	public int name;

	public ConstantModule(int name)
	{
		this.name = name;
	}

	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_MODULE;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[3];
		b[0] = this.type().code();
		b[1] = (byte) ((this.name >>> 8) & 0XFF);
		b[2] = (byte) (this.name & 0XFF);
		return b;
	}

	@Override
	public int length()
	{
		return 3;
	}
}
