package org.mve.util.asm.file;

public class ConstantDynamic extends ConstantPoolElement
{
	private final short bootstrapMethodAttributeIndex;
	private final short nameAndTypeIndex;

	public ConstantDynamic(short bootstrapMethodAttributeIndex, short nameAndTypeIndex)
	{
		this.bootstrapMethodAttributeIndex = bootstrapMethodAttributeIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	public short getBootstrapMethodAttributeIndex()
	{
		return bootstrapMethodAttributeIndex;
	}

	public short getNameAndTypeIndex()
	{
		return nameAndTypeIndex;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_DYNAMIC;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[5];
		b[0] = this.getType().getCode();
		b[1] = (byte) ((this.bootstrapMethodAttributeIndex >>> 8) & 0XFF);
		b[2] = (byte) (this.bootstrapMethodAttributeIndex & 0XFF);
		b[3] = (byte) ((this.nameAndTypeIndex >>> 8) & 0XFF);
		b[4] = (byte) (this.nameAndTypeIndex & 0XFF);
		return b;
	}
}
