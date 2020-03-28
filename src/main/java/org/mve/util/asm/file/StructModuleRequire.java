package org.mve.util.asm.file;

import org.mve.util.Binary;

public class StructModuleRequire implements Binary
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

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[6];
		b[0] = (byte) ((this.requiresIndex >>> 8) & 0XFF);
		b[1] = (byte) (this.requiresIndex & 0XFF);
		b[2] = (byte) ((this.requiresFlags >>> 8) & 0XFF);
		b[3] = (byte) (this.requiresFlags & 0XFF);
		b[4] = (byte) ((this.requiresVersionIndex >>> 8) & 0XFF);
		b[5] = (byte) (this.requiresVersionIndex & 0XFF);
		return b;
	}
}
