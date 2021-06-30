package org.mve.asm.file.constant;

public class ConstantNull extends Constant
{
	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_NULL;
	}

	@Override
	public int length()
	{
		return 0;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[0];
	}
}
