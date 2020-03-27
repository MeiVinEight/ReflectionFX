package org.mve.util.asm.file;

public class ConstantNameAndType extends ConstantPoolElement
{
	private final short nameIndex;
	private final short descriptorIndex;

	public ConstantNameAndType(short nameIndex, short descriptorIndex)
	{
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}

	public short getNameIndex()
	{
		return nameIndex;
	}

	public short getDescriptorIndex()
	{
		return descriptorIndex;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_NAME_AND_TYPE;
	}
}
