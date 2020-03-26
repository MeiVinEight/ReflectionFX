package org.mve.util.asm;

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
		return 2 + (4 * this.lineNumberTableLength);
	}
}
