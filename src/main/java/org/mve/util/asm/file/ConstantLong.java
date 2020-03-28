package org.mve.util.asm.file;

public class ConstantLong extends ConstantPoolElement
{
	private final long value;

	public ConstantLong(long value)
	{
		this.value = value;
	}

	public long getValue()
	{
		return value;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_LONG;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[9];
		b[0] = this.getType().getCode();
		b[1] = (byte) ((this.value >>> 56) & 0XFF);
		b[2] = (byte) ((this.value >>> 48) & 0XFF);
		b[3] = (byte) ((this.value >>> 40) & 0XFF);
		b[4] = (byte) ((this.value >>> 32) & 0XFF);
		b[5] = (byte) ((this.value >>> 24) & 0XFF);
		b[6] = (byte) ((this.value >>> 16) & 0XFF);
		b[7] = (byte) ((this.value >>> 8) & 0XFF);
		b[8] = (byte) (this.value & 0XFF);
		return b;
	}
}
