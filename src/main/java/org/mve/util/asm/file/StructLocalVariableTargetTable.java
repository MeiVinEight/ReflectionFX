package org.mve.util.asm.file;

import org.mve.util.Binary;

public class StructLocalVariableTargetTable implements Binary
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

	@Override
	public byte[] toByteArray()
	{
		return new byte[]
		{
			(byte) ((this.startPc >>> 8) & 0XFF),
			(byte) (this.startPc & 0XFF),
			(byte) ((this.length >>> 8) & 0XFF),
			(byte) (this.length & 0XFF),
			(byte) ((this.index >>> 8) & 0XFF),
			(byte) (this.index & 0XFF)
		};
	}
}
