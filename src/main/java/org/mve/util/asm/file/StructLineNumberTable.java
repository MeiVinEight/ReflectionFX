package org.mve.util.asm.file;

public class StructLineNumberTable
{
	private short startPc;
	private short lineNumber;

	public short getStartPc()
	{
		return startPc;
	}

	public void setStartPc(short startPc)
	{
		this.startPc = startPc;
	}

	public short getLineNumber()
	{
		return lineNumber;
	}

	public void setLineNumber(short lineNumber)
	{
		this.lineNumber = lineNumber;
	}
}
