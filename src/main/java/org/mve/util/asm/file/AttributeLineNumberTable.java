package org.mve.util.asm.file;

import java.util.Objects;

public class AttributeLineNumberTable extends Attribute
{
	private short lineNumberTableLength;
	private StructLineNumberTable[] lineNumberTables = new StructLineNumberTable[0];

	public AttributeLineNumberTable(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getLineNumberTableLength()
	{
		return lineNumberTableLength;
	}

	public void addLineNumberTable(StructLineNumberTable table)
	{
		StructLineNumberTable[] arr = new StructLineNumberTable[++this.lineNumberTableLength];
		System.arraycopy(this.lineNumberTables, 0, arr, 0, this.lineNumberTables.length);
		arr[this.lineNumberTables.length] = Objects.requireNonNull(table);
		this.lineNumberTables = arr;
	}

	public void setLineNumberTable(int index, StructLineNumberTable table)
	{
		this.lineNumberTables[index] = table;
	}

	public StructLineNumberTable getLineNumberTable(int index)
	{
		return this.lineNumberTables[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.LINE_NUMBER_TABLE;
	}

	@Override
	public int getLength()
	{
		return 8 + (4 * this.lineNumberTableLength);
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
		b[index++] = (byte) ((this.lineNumberTableLength >>> 8) & 0XFF);
		b[index++] = (byte) (this.lineNumberTableLength & 0XFF);
		for (StructLineNumberTable s : this.lineNumberTables)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 4);
			index+=4;
		}
		return b;
	}
}
