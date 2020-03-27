package org.mve.util.asm.file;

public class ConstantModule extends ConstantPoolElement
{
	private final short nameIndex;

	public ConstantModule(short nameIndex)
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
		return ConstantPoolElementType.CONSTANT_MODULE;
	}
}
