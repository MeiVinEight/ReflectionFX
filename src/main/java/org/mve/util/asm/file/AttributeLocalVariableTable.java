package org.mve.util.asm.file;

import java.util.Objects;

public class AttributeLocalVariableTable extends Attribute
{
	private short localVariableTableLength;
	private StructLocalVariableTable[] localVariableTables = new StructLocalVariableTable[0];

	public AttributeLocalVariableTable(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getLocalVariableTableLength()
	{
		return localVariableTableLength;
	}

	public void addLocalVariableTable(StructLocalVariableTable table)
	{
		StructLocalVariableTable[] arr = new StructLocalVariableTable[++this.localVariableTableLength];
		System.arraycopy(this.localVariableTables, 0, arr, 0, this.localVariableTables.length);
		arr[this.localVariableTables.length] = Objects.requireNonNull(table);
		this.localVariableTables = arr;
	}

	public void setLocalVariableTable(int index, StructLocalVariableTable table)
	{
		this.localVariableTables[index] = Objects.requireNonNull(table);
	}

	public StructLocalVariableTable getLocalVariableTable(int index)
	{
		return this.localVariableTables[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.LOCAL_VARIABLE_TABLE;
	}

	@Override
	public int getLength()
	{
		return 2 + (10 * this.localVariableTableLength);
	}
}
