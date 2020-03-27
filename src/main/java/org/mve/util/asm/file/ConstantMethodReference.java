package org.mve.util.asm.file;

public class ConstantMethodReference extends ConstantPoolElement
{
	private final short classIndex;
	private final short nameAndTypeIndex;

	public ConstantMethodReference(short classIndex, short nameAndTypeIndex)
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
		return ConstantPoolElementType.CONSTANT_METHOD_REFERENCE;
	}
}
