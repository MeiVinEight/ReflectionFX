package org.mve.util.asm.file;

public class StructLocalVariableTargetTable
{
	private short startPc;
	private short length;
	private short index;

	public short getStartPc()
	{
		return startPc;
	}

	public void setStartPc(short startPc)
	{
		this.startPc = startPc;
	}

	public short getLength()
	{
		return length;
	}

	public void setLength(short length)
	{
		this.length = length;
	}

	public short getIndex()
	{
		return index;
	}

	public void setIndex(short index)
	{
		this.index = index;
	}
}
