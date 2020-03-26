package org.mve.util.asm;

public class StructModuleRequire
{
	private short requiresIndex;
	private short requiresFlags;
	private short requiresVersionIndex;

	public short getRequiresIndex()
	{
		return requiresIndex;
	}

	public void setRequiresIndex(short requiresIndex)
	{
		this.requiresIndex = requiresIndex;
	}

	public short getRequiresFlags()
	{
		return requiresFlags;
	}

	public void setRequiresFlags(short requiresFlags)
	{
		this.requiresFlags = requiresFlags;
	}

	public short getRequiresVersionIndex()
	{
		return requiresVersionIndex;
	}

	public void setRequiresVersionIndex(short requiresVersionIndex)
	{
		this.requiresVersionIndex = requiresVersionIndex;
	}
}
