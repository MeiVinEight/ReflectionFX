package org.mve.asm.file.constant;

public class ConstantMethodReference extends Constant
{
	public int clazz;
	public int nameAndType;

	public ConstantMethodReference(int clazz, int nameAndType)
	{
		this.clazz = clazz;
		this.nameAndType = nameAndType;
	}

	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_METHOD_REFERENCE;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[5];
		b[0] = this.type().code();
		b[1] = (byte) ((this.clazz >>> 8) & 0XFF);
		b[2] = (byte) (this.clazz & 0XFF);
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
