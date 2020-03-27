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
}
