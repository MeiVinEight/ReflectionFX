package org.mve.util.asm;

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
}
