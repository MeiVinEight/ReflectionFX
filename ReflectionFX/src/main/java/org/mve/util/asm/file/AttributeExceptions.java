package org.mve.util.asm.file;

public class AttributeExceptions extends Attribute
{
	private short exceptionCount;
	private short[] exceptions = new short[0];

	public AttributeExceptions(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public void addException(short cpIndex)
	{
		short[] arr = new short[++this.exceptionCount];
		System.arraycopy(this.exceptions, 0, arr, 0, this.exceptions.length);
		arr[this.exceptions.length] = cpIndex;
		this.exceptions = arr;
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.EXCEPTIONS;
	}

	@Override
	public int getLength()
	{
		return 8 + (2 * this.exceptionCount);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[index++] = (byte) (this.getAttributeNameIndex() & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) ((this.exceptionCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.exceptionCount & 0XFF);
		for (short s : this.exceptions)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
