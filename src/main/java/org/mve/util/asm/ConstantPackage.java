package org.mve.util.asm;

public class ConstantPackage extends ConstantPoolElement
{
	private final short nameIndex;

	public ConstantPackage(short nameIndex)
	{
		this.nameIndex = nameIndex;
	}

	public short getNameIndex()
	{
		return nameIndex;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_PACKAGE;
	}
}
