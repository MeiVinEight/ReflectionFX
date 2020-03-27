package org.mve.util.asm.file;

public class ElementValueClass extends ElementValue
{
	private short classIndex;

	public ElementValueClass(byte type)
	{
		super(type);
	}

	public short getClassIndex()
	{
		return classIndex;
	}

	public void setClassIndex(short classIndex)
	{
		this.classIndex = classIndex;
	}

	@Override
	public int getLength()
	{
		return 3;
	}
}
