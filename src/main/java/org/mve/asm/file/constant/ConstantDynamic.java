package org.mve.asm.file.constant;

public class ConstantDynamic extends Constant
{
	public int bootstrap;
	public int nameAndType;

	public ConstantDynamic(int bootstrap, int nameAndType)
	{
		this.bootstrap = bootstrap;
		this.nameAndType = nameAndType;
	}

	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_DYNAMIC;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[5];
		b[0] = this.type().code();
		b[1] = (byte) ((this.bootstrap >>> 8) & 0XFF);
		b[2] = (byte) (this.bootstrap & 0XFF);
		b[3] = (byte) ((this.nameAndType >>> 8) & 0XFF);
		b[4] = (byte) (this.nameAndType & 0XFF);
		return b;
	}

	@Override
	public int length()
	{
		return 5;
	}
}
