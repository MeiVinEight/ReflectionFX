package org.mve.asm.file.attribute;

import org.mve.asm.file.attribute.code.exception.Exception;

import java.util.Arrays;

public class AttributeCode extends Attribute
{
	public int stack;
	public int local;
	public byte[] code;
	public Exception[] exception = new Exception[0];
	public Attribute[] attribute = new Attribute[0];

	public void exception(Exception exception)
	{
		this.exception = Arrays.copyOf(this.exception, this.exception.length + 1);
		this.exception[this.exception.length - 1] = exception;
	}

	public void attribute(Attribute attribute)
	{
		this.attribute = Arrays.copyOf(this.attribute, this.attribute.length + 1);
		this.attribute[this.attribute.length - 1] = attribute;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.CODE;
	}

	@Override
	public int length()
	{
		int len = 18 + this.code.length + (8 * this.exception.length);
		for (Attribute a : this.attribute) len += a.length();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = (byte) ((name >>> 8) & 0XFF);
		b[index++] = (byte) (name & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) ((this.stack >>> 8) & 0XFF);
		b[index++] = (byte) (this.stack & 0XFF);
		b[index++] = (byte) ((this.local >>> 8) & 0XFF);
		b[index++] = (byte) (this.local & 0XFF);
		b[index++] = (byte) ((this.code.length >>> 24) & 0XFF);
		b[index++] = (byte) ((this.code.length >>> 16) & 0XFF);
		b[index++] = (byte) ((this.code.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.code.length & 0XFF);
		System.arraycopy(this.code, 0, b, index, this.code.length);
		index+=this.code.length;
		b[index++] = (byte) ((this.exception.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.exception.length & 0XFF);
		for (Exception s : this.exception)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 8);
			index+=8;
		}
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
