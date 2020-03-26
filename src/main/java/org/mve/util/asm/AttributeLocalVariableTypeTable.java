package org.mve.util.asm;

import java.util.Objects;

public class AttributeLocalVariableTypeTable extends Attribute
{
	private short localVariableTypeTableLength;
	private StructLocalVariableTypeTable[] localVariableTypeTables = new StructLocalVariableTypeTable[0];

	public AttributeLocalVariableTypeTable(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getLocalVariableTypeTableLength()
	{
		return localVariableTypeTableLength;
	}

	public void addLocalVariableTypeTable(StructLocalVariableTypeTable table)
	{
		StructLocalVariableTypeTable[] arr = new StructLocalVariableTypeTable[++this.localVariableTypeTableLength];
		System.arraycopy(this.localVariableTypeTables, 0, arr, 0, this.localVariableTypeTables.length);
		arr[this.localVariableTypeTables.length] = Objects.requireNonNull(table);
		this.localVariableTypeTables = arr;
	}

	public void setLocalVariableTypeTable(int index, StructLocalVariableTypeTable table)
	{
		this.localVariableTypeTables[index] = Objects.requireNonNull(table);
	}

	public StructLocalVariableTypeTable getLocalVariableTypeTable(int index)
	{
		return this.localVariableTypeTables[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.LOCAL_VARIABLE_TYPE_TABLE;
	}

	@Override
	public int getLength()
	{
		return 2 + (10 * this.localVariableTypeTableLength);
	}
}
