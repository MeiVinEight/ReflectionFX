package org.mve.asm.file;

import org.mve.util.Binary;

public class StructModuleOpen implements Binary
{
	private short openIndex;
	private short openFlags;
	private short openToCount;
	private short[] openTo = new short[0];

	public short getOpenIndex()
	{
		return openIndex;
	}

	public void setOpenIndex(short openIndex)
	{
		this.openIndex = openIndex;
	}

	public short getOpenFlags()
	{
		return openFlags;
	}

	public void setOpenFlags(short openFlags)
	{
		this.openFlags = openFlags;
	}

	public short getOpenToCount()
	{
		return openToCount;
	}

	public void addOpenTo(short cpIndex)
	{
		short[] arr = new short[this.openToCount+1];
		System.arraycopy(this.openTo, 0, arr, 0, this.openToCount);
		arr[this.openToCount] = cpIndex;
		this.openTo = arr;
		this.openToCount++;
	}

	public void setOpenTo(int index, short cpIndex)
	{
		this.openTo[index] = cpIndex;
	}

	public short getOpenTo(int index)
	{
		return this.openTo[index];
	}

	public int getLength()
	{
		return 6 + (2 * this.openToCount);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = (byte) ((this.openIndex >>> 8) & 0XFF);
		b[index++] = (byte) (this.openIndex & 0XFF);
		b[index++] = (byte) ((this.openFlags >>> 8) & 0XFF);
		b[index++] = (byte) (this.openFlags & 0XFF);
		b[index++] = (byte) ((this.openToCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.openToCount & 0XFF);
		for (short s : this.openTo)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
