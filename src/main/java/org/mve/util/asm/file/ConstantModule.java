package org.mve.util.asm.file;

public class ConstantModule extends ConstantPoolElement
{
	private final short nameIndex;

	public ConstantModule(short nameIndex)
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
		return ConstantPoolElementType.CONSTANT_MODULE;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[3];
		b[0] = this.getType().getCode();
		b[1] = (byte) ((this.nameIndex >>> 8) & 0XFF);
		b[2] = (byte) (this.nameIndex & 0XFF);
		return b;
	}
}
