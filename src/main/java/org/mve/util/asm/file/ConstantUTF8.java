package org.mve.util.asm.file;

public class ConstantUTF8 extends ConstantPoolElement
{
	private final short length;
	private final String utf8;

	public ConstantUTF8(short length, String utf8)
	{
		this.length = length;
		this.utf8 = utf8;
	}

	public ConstantUTF8(String utf8)
	{
		this((short) utf8.length(), utf8);
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

	@Override
	public byte[] toByteArray()
	{
		int len = this.length + 3;
		byte[] b = new byte[len];
		b[0] = this.getType().getCode();
		b[1] = (byte) ((this.length >>> 8) & 0XFF);
		b[2] = (byte) (this.length & 0XFF);
		System.arraycopy(this.utf8.getBytes(), 0, b, 3, this.length);
		return b;
	}
}
