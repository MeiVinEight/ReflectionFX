package org.mve.util.asm.file;

public class ElementValueEnum extends ElementValue
{
	private short typeNameIndex;
	private short constNameIndex;

	public ElementValueEnum(byte type)
	{
		super(type);
	}

	public short getTypeNameIndex()
	{
		return typeNameIndex;
	}

	public void setTypeNameIndex(short typeNameIndex)
	{
		this.typeNameIndex = typeNameIndex;
	}

	public short getConstNameIndex()
	{
		return constNameIndex;
	}

	public void setConstNameIndex(short constNameIndex)
	{
		this.constNameIndex = constNameIndex;
	}

	@Override
	public int getLength()
	{
		return 5;
	}
}
