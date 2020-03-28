package org.mve.util.asm.file;

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
		return 8 + (10 * this.localVariableTypeTableLength);
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
		b[index++] = (byte) ((this.localVariableTypeTableLength >>> 8) & 0XFF);
		b[index++] = (byte) (this.localVariableTypeTableLength & 0XFF);
		for (StructLocalVariableTypeTable s : this.localVariableTypeTables)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 10);
			index+=10;
		}
		return b;
	}
}
