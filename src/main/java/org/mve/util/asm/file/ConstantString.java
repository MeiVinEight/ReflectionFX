package org.mve.util.asm.file;

public class ConstantString extends ConstantPoolElement
{
	private final short stringIndex;

	public ConstantString(short stringIndex)
	{
		this.stringIndex = stringIndex;
	}

	public short getStringIndex()
	{
		return stringIndex;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_STRING;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[3];
		b[0] = this.getType().getCode();
		b[1] = (byte) ((this.stringIndex >>> 8) & 0XFF);
		b[2] = (byte) (this.stringIndex & 0XFF);
		return b;
	}
}
