package org.mve.asm.file;

public class ConstantMethodReference extends ConstantPoolElement
{
	private final short classIndex;
	private final short nameAndTypeIndex;

	public ConstantMethodReference(short classIndex, short nameAndTypeIndex)
	{
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	public short getClassIndex()
	{
		return classIndex;
	}

	public short getNameAndTypeIndex()
	{
		return nameAndTypeIndex;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_METHOD_REFERENCE;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[5];
		b[0] = this.getType().getCode();
		b[1] = (byte) ((this.classIndex >>> 8) & 0XFF);
		b[2] = (byte) (this.classIndex & 0XFF);
		b[3] = (byte) ((this.nameAndTypeIndex >>> 8) & 0XFF);
		b[4] = (byte) (this.nameAndTypeIndex & 0XFF);
		return b;
	}
}
