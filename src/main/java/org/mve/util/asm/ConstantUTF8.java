package org.mve.util.asm;

public class ConstantUTF8 extends ConstantPoolElement
{
	private final short length;
	private final String utf8;

	public ConstantUTF8(short length, String utf8)
	{
		this.length = length;
		this.utf8 = utf8;
	}

	public short getLength()
	{
		return length;
	}

	public String getUTF8()
	{
		return utf8;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_UTF8;
	}
}
