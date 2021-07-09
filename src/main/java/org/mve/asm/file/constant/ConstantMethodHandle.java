package org.mve.asm.file.constant;

public class ConstantMethodHandle extends Constant
{
	public int kind;
	public int reference;

	public ConstantMethodHandle(int kind, int reference)
	{
		this.kind = kind;
		this.reference = reference;
	}

	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_METHOD_HANDLE;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[4];
		b[0] = this.type().code();
		b[1] = (byte) this.kind;
		b[2] = (byte) ((this.reference >>> 8) & 0XFF);
		b[3] = (byte) (this.reference & 0XFF);
		return b;
	}

	@Override
	public int length()
	{
		return 4;
	}
}
