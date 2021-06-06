package org.mve.asm.file;

import org.mve.util.Binary;

public class StructPath implements Binary
{
	private byte typePathKind;
	private byte typeArgumentIndex;

	public byte getTypePathKind()
	{
		return typePathKind;
	}

	public void setTypePathKind(byte typePathKind)
	{
		this.typePathKind = typePathKind;
	}

	public byte getTypeArgumentIndex()
	{
		return typeArgumentIndex;
	}

	public void setTypeArgumentIndex(byte typeArgumentIndex)
	{
		this.typeArgumentIndex = typeArgumentIndex;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{typePathKind, typeArgumentIndex};
	}
}
