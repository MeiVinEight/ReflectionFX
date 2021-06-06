package org.mve.asm.file;

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

	@Override
	public byte[] toByteArray()
	{
		return new byte[]
		{
			this.getType(),
			(byte) ((this.typeNameIndex >>> 8) & 0XFF),
			(byte) (this.typeNameIndex & 0XFF),
			(byte) ((this.constNameIndex >>> 8) & 0XFF),
			(byte) (this.constNameIndex & 0XFF)
		};
	}
}
