package org.mve.asm.file;

import org.mve.asm.file.attribute.Attribute;

import java.util.Arrays;

public class Field
{
	public int access;
	public int name;
	public int type;
	public Attribute[] attribute = new Attribute[0];

	public void attribute(Attribute attribute)
	{
		this.attribute = Arrays.copyOf(this.attribute, this.attribute.length+1);
		this.attribute[this.attribute.length-1] = attribute;
	}

	public byte[] toByteArray()
	{
		int len = 8;
		for (Attribute a : this.attribute) len += a.length();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((this.access >>> 8) & 0XFF);
		b[index++] = (byte) (this.access & 0XFF);
		b[index++] = (byte) ((this.name >>> 8) & 0XFF);
		b[index++] = (byte) (this.name & 0XFF);
		b[index++] = (byte) ((this.type >>> 8) & 0XFF);
		b[index++] = (byte) (this.type & 0XFF);
		b[index++] = (byte) ((this.attribute.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.attribute.length & 0XFF);
		for (Attribute a : this.attribute)
		{
			int l = a.length();
			System.arraycopy(a.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
