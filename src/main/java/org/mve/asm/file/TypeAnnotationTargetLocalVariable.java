package org.mve.asm.file;

import java.util.Objects;

public class TypeAnnotationTargetLocalVariable extends TypeAnnotationTarget
{
	private short tableLength;
	private StructLocalVariableTargetTable[] tables = new StructLocalVariableTargetTable[0];

	public TypeAnnotationTargetLocalVariable(byte targetType)
	{
		super(targetType);
	}

	public short getTableLength()
	{
		return tableLength;
	}

	public void addLocalVariableTargetTable(StructLocalVariableTargetTable table)
	{
		StructLocalVariableTargetTable[] arr = new StructLocalVariableTargetTable[this.tableLength+1];
		System.arraycopy(this.tables, 0, arr, 0, this.tableLength);
		arr[this.tableLength] = Objects.requireNonNull(table);
		this.tables = arr;
		this.tableLength++;
	}

	public void setLocalVariableTargetTable(int index, StructLocalVariableTargetTable table)
	{
		this.tables[index] = Objects.requireNonNull(table);
	}

	public StructLocalVariableTargetTable getLocalVariableTargetTable(int index)
	{
		return this.tables[index];
	}

	@Override
	public int getLength()
	{
		return 2 + (6 * this.tableLength);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((this.tableLength >>> 8) & 0XFF);
		b[index++] = (byte) (this.tableLength & 0XFF);
		for (StructLocalVariableTargetTable s : this.tables)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 6);
			index+=6;
		}
		return b;
	}
}
