package org.mve.util.asm;

public class ElementValueConstant extends ElementValue
{
	private short constantValueIndex;

	public ElementValueConstant(byte type)
	{
		super(type);
	}

	public short getConstantValueIndex()
	{
		return constantValueIndex;
	}

	public void setConstantValueIndex(short constantValueIndex)
	{
		this.constantValueIndex = constantValueIndex;
	}

	@Override
	public int getLength()
	{
		return 3;
	}
}
