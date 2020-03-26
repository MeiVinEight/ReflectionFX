package org.mve.util.asm;

public class StructMethodParameter
{
	private short nameIndex;
	private short accessFlag;

	public short getNameIndex()
	{
		return nameIndex;
	}

	public void setNameIndex(short nameIndex)
	{
		this.nameIndex = nameIndex;
	}

	public short getAccessFlag()
	{
		return accessFlag;
	}

	public void setAccessFlag(short accessFlag)
	{
		this.accessFlag = accessFlag;
	}
}
