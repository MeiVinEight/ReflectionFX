package org.mve.util.asm.file;

public class StructBootstrapMethod
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
}
