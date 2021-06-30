package org.mve.asm.file.attribute;

import java.util.Arrays;

public class AttributeExceptions extends Attribute
{
	public int[] exception = new int[0];

	public void exception(int cp)
	{
		this.exception = Arrays.copyOf(this.exception, this.exception.length + 1);
		this.exception[this.exception.length - 1] = cp;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.EXCEPTIONS;
	}

	@Override
	public int length()
	{
		return 8 + (2 * this.exception.length);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((name >>> 8) & 0XFF);
		b[index++] = (byte) (name & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) ((this.exception.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.exception.length & 0XFF);
		for (int s : this.exception)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
