package org.mve.util.asm;

public class ConstantFieldReference extends ConstantPoolElement
{
	private final short classIndex;
	private final short nameAndTypeIndex;

	public ConstantFieldReference(short classIndex, short nameAndTypeIndex)
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
		return ConstantPoolElementType.CONSTANT_FIELD_REFERENCE;
	}
}
