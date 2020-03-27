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
}
