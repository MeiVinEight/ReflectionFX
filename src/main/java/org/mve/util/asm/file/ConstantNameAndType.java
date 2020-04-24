package org.mve.util.asm.file;

public class ConstantNameAndType extends ConstantPoolElement
{
	private final short nameIndex;
	private final short typeIndex;

	public ConstantNameAndType(short nameIndex, short typeIndex)
	{
		this.nameIndex = nameIndex;
		this.typeIndex = typeIndex;
	}

	public short getNameIndex()
	{
		return nameIndex;
	}

	public short getTypeIndex()
	{
		return typeIndex;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_NAME_AND_TYPE;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[5];
		b[0] = this.getType().getCode();
		b[1] = (byte) ((this.nameIndex >>> 8) & 0XFF);
		b[2] = (byte) (this.nameIndex & 0XFF);
		b[3] = (byte) ((this.typeIndex >>> 8) & 0XFF);
		b[4] = (byte) (this.typeIndex & 0XFF);
		return b;
	}
}
