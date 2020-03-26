package org.mve.util.asm;

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
		StructExceptionTable[] exceptionTables = new StructExceptionTable[++this.exceptionTableLength];
		System.arraycopy(this.exceptionTable, 0, exceptionTables, 0, this.attributes.length);
		exceptionTables[this.exceptionTableLength-1] = table;
		this.exceptionTable = exceptionTables;
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
		int len = 12 + this.codeLength + (8 * this.exceptionTableLength);
		len += (this.attributes.length * 6);
		for (Attribute a : this.attributes) len += a.getLength();
		return len;
	}
}
