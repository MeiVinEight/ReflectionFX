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

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{this.getType(), (byte) ((this.classIndex >>> 8) & 0XFF), (byte) (this.classIndex & 0XFF)};
	}
}
