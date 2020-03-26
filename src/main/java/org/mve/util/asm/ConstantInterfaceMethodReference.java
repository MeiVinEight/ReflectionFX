package org.mve.util.asm;

public class ConstantInterfaceMethodReference extends ConstantPoolElement
{
	private final short classIndex;
	private final short nameAndTypeIndex;

	public ConstantInterfaceMethodReference(short classIndex, short nameAndTypeIndex)
	{
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	public short getClassIndex()
	{
		return classIndex;
	}

	public short getNameAndTypeIndex()
	{
		return nameAndTypeIndex;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_INTERFACE_METHOD_REFERENCE;
	}
}
