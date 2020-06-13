package org.mve.util.asm.file;

import org.mve.util.Binary;

public class StructModuleProvide implements Binary
{
	private short provideIndex;
	private short provideWithCount;
	private short[] provideWith = new short[0];

	public short getProvideIndex()
	{
		return provideIndex;
	}

	public void setProvideIndex(short provideIndex)
	{
		this.provideIndex = provideIndex;
	}

	public short getProvideWithCount()
	{
		return provideWithCount;
	}

	public void addProvideWith(short cpIndex)
	{
		short[] arr = new short[this.provideWithCount+1];
		System.arraycopy(this.provideWith, 0, arr, 0, this.provideWithCount);
		arr[this.provideWithCount] = cpIndex;
		this.provideWith = arr;
		this.provideWithCount++;
	}

	public void setProvideWith(int index, short cpIndex)
	{
		this.provideWith[index] = cpIndex;
	}

	public short getProvideWith(int index)
	{
		return this.provideWith[index];
	}

	public int getLength()
	{
		return 4 + (2 * this.provideWithCount);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = (byte) ((this.provideIndex >>> 8) & 0XFF);
		b[index++] = (byte) (this.provideIndex & 0XFF);
		b[index++] = (byte) ((this.provideWithCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.provideWithCount & 0XFF);
		for (short s : this.provideWith)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
