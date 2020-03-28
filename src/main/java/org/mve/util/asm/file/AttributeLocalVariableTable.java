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
		return 8 + (10 * this.localVariableTableLength);
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
		b[index++] = (byte) ((this.localVariableTableLength >>> 8) & 0XFF);
		b[index++] = (byte) (this.localVariableTableLength & 0XFF);
		for (StructLocalVariableTable s : this.localVariableTables)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 10);
			index+=10;
		}
		return b;
	}
}
