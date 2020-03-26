package org.mve.util.asm;

public class StructPath
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
}
