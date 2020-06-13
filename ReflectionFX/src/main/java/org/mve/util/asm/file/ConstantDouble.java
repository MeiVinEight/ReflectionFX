package org.mve.util.asm.file;

public class ConstantDouble extends ConstantPoolElement
{
	private final double value;

	public ConstantDouble(double value)
	{
		this.value = value;
	}

	public double getValue()
	{
		return value;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_DOUBLE;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[9];
		long l = Double.doubleToLongBits(this.value);
		b[0] = this.getType().getCode();
		b[1] = (byte) ((l >>> 56) & 0XFF);
		b[2] = (byte) ((l >>> 48) & 0XFF);
		b[3] = (byte) ((l >>> 40) & 0XFF);
		b[4] = (byte) ((l >>> 32) & 0XFF);
		b[5] = (byte) ((l >>> 24) & 0XFF);
		b[6] = (byte) ((l >>> 16) & 0XFF);
		b[7] = (byte) ((l >>> 8) & 0XFF);
		b[8] = (byte) (l & 0XFF);
		return b;
	}
}
