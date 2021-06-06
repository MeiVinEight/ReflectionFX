package org.mve.asm.file;

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

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{this.getType(), (byte) ((this.constantValueIndex >>> 8) & 0XFF), (byte) (this.constantValueIndex & 0XFF)};
	}
}
