package org.mve.util.asm.file;

public class ConstantClass extends ConstantPoolElement
{
	private final short nameIndex;

	public ConstantClass(short nameIndex)
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
		return ConstantPoolElementType.CONSTANT_CLASS;
	}
}
