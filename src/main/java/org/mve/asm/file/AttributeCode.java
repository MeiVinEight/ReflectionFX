package org.mve.asm.file;

import java.util.Objects;

public class AttributeCode extends Attribute
{
	private short maxStack;
	private short maxLocals;
	private int codeLength;
	private byte[] code;
	private short exceptionTableLength;
	private StructExceptionTable[] exceptionTable = new StructExceptionTable[0];
	private short attributesCount;
	private Attribute[] attributes = new Attribute[0];

	public AttributeCode(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getMaxStack()
	{
		return maxStack;
	}

	public void setMaxStack(short maxStack)
	{
		this.maxStack = maxStack;
	}

	public short getMaxLocals()
	{
		return maxLocals;
	}

	public void setMaxLocals(short maxLocals)
	{
		this.maxLocals = maxLocals;
	}

	public int getCodeLength()
	{
		return codeLength;
	}

	public byte[] getCode()
	{
		return code;
	}

	public void setCode(byte[] code)
	{
		this.code = code;
		this.codeLength = code.length;
	}

	public short getExceptionTableLength()
	{
		return exceptionTableLength;
	}

	public StructExceptionTable getExceptionTable(int index)
	{
		return exceptionTable[index];
	}

	public void setExceptionTable(int index, StructExceptionTable exceptionTable)
	{
		this.exceptionTable[index] = exceptionTable;
	}

	public void addExceptionTable(StructExceptionTable table)
	{
		StructExceptionTable[] exceptionTables = new StructExceptionTable[this.exceptionTableLength+1];
		System.arraycopy(this.exceptionTable, 0, exceptionTables, 0, this.exceptionTableLength);
		exceptionTables[this.exceptionTableLength] = Objects.requireNonNull(table);
		this.exceptionTable = exceptionTables;
		this.exceptionTableLength++;
	}

	public short getAttributesCount()
	{
		return attributesCount;
	}

	public Attribute getAttribute(int index)
	{
		return attributes[index];
	}

	public void setAttributes(int index, Attribute attribute)
	{
		this.attributes[index] = attribute;
	}

	public void addAttribute(Attribute attribute)
	{
		Attribute[] attributes = new Attribute[++this.attributesCount];
		System.arraycopy(this.attributes, 0, attributes, 0, this.attributes.length);
		attributes[attributes.length-1] = attribute;
		this.attributes = attributes;
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.CODE;
	}

	@Override
	public int getLength()
	{
		int len = 18 + this.codeLength + (8 * this.exceptionTableLength);
		for (Attribute a : this.attributes) len += a.getLength();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[index++] = (byte) (this.getAttributeNameIndex() & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) ((this.maxStack >>> 8) & 0XFF);
		b[index++] = (byte) (this.maxStack & 0XFF);
		b[index++] = (byte) ((this.maxLocals >>> 8) & 0XFF);
		b[index++] = (byte) (this.maxLocals & 0XFF);
		b[index++] = (byte) ((this.codeLength >>> 24) & 0XFF);
		b[index++] = (byte) ((this.codeLength >>> 16) & 0XFF);
		b[index++] = (byte) ((this.codeLength >>> 8) & 0XFF);
		b[index++] = (byte) (this.codeLength & 0XFF);
		System.arraycopy(this.code, 0, b, index, this.codeLength);
		index+=this.codeLength;
		b[index++] = (byte) ((this.exceptionTableLength >>> 8) & 0XFF);
		b[index++] = (byte) (this.exceptionTableLength & 0XFF);
		for (StructExceptionTable s : this.exceptionTable)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 8);
			index+=8;
		}
		b[index++] = (byte) ((this.attributesCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.attributesCount & 0XFF);
		for (Attribute a : this.attributes)
		{
			int l = a.getLength();
			System.arraycopy(a.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
