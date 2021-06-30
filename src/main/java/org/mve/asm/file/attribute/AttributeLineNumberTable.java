package org.mve.asm.file.attribute;

import org.mve.asm.file.attribute.line.LineNumber;

import java.util.Arrays;

public class AttributeLineNumberTable extends Attribute
{
	private LineNumber[] line = new LineNumber[0];

	public void line(LineNumber line)
	{
		this.line = Arrays.copyOf(this.line, this.line.length + 1);
		this.line[this.line.length - 1] = line;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.LINE_NUMBER_TABLE;
	}

	@Override
	public int length()
	{
		return 8 + (4 * this.line.length);
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
		b[index++] = (byte) ((this.line.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.line.length & 0XFF);
		for (LineNumber s : this.line)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 4);
			index+=4;
		}
		return b;
	}
}
