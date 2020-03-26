package org.mve.util.asm;

public class ConstantMethodType extends ConstantPoolElement
{
	private final short descriptorIndex;

	public ConstantMethodType(short descriptorIndex)
	{
		this.descriptorIndex = descriptorIndex;
	}

	public short getDescriptorIndex()
	{
		return descriptorIndex;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_METHOD_TYPE;
	}
}
