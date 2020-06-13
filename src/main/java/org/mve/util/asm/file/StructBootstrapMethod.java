package org.mve.util.asm.file;

import org.mve.util.Binary;

public class StructBootstrapMethod implements Binary
{
	private short bootstrapMethodReference;
	private short bootstrapArgumentCount;
	private short[] bootstrapMethodArguments = new short[0];

	public short getBootstrapMethodReference()
	{
		return bootstrapMethodReference;
	}

	public void setBootstrapMethodReference(short bootstrapMethodReference)
	{
		this.bootstrapMethodReference = bootstrapMethodReference;
	}

	public short getBootstrapArgumentCount()
	{
		return bootstrapArgumentCount;
	}

	public void addBootstrapMethodArgument(short cpIndex)
	{
		short[] arr = new short[this.bootstrapArgumentCount+1];
		System.arraycopy(this.bootstrapMethodArguments, 0, arr, 0, this.bootstrapArgumentCount);
		arr[this.bootstrapArgumentCount] = cpIndex;
		this.bootstrapMethodArguments = arr;
		this.bootstrapArgumentCount++;
	}

	public void setBootstrapMethodArgument(int index, short cpIndex)
	{
		this.bootstrapMethodArguments[index] = cpIndex;
	}

	public short getBootstrapMethodArgument(int index)
	{
		return this.bootstrapMethodArguments[index];
	}

	public int getLength()
	{
		return 4 + (2 * this.bootstrapArgumentCount);
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[this.getLength()];
		int index = 0;
		b[index++] = (byte) ((this.bootstrapMethodReference >>> 8) & 0XFF);
		b[index++] = (byte) (this.bootstrapMethodReference & 0XFF);
		b[index++] = (byte) ((this.bootstrapArgumentCount >>> 8) & 0XFF);
		b[index++] = (byte) (bootstrapArgumentCount & 0XFF);
		for (short s : this.bootstrapMethodArguments)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
