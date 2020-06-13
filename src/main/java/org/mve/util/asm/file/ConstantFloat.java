package org.mve.util.asm.file;

public class ConstantFloat extends ConstantPoolElement
{
	private final float value;

	public ConstantFloat(float value)
	{
		this.value = value;
	}

	public float getValue()
	{
		return value;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_FLOAT;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[5];
		int i =  Float.floatToIntBits(this.value);
		b[0] = this.getType().getCode();
		b[1] = (byte) ((i >>> 24) & 0XFF);
		b[2] = (byte) ((i >>> 16) & 0XFF);
		b[3] = (byte) ((i >>> 8) & 0XFF);
		b[4] = (byte) (i & 0XFF);
		return b;
	}
}
