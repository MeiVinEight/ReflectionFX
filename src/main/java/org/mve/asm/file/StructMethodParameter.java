package org.mve.asm.file;

import org.mve.util.Binary;

public class StructMethodParameter implements Binary
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

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[4];
		b[0] = (byte) ((this.nameIndex >>> 8) & 0XFF);
		b[1] = (byte) (this.nameIndex & 0XFF);
		b[2] = (byte) ((this.accessFlag >>> 8) & 0XFF);
		b[3] = (byte) (this.accessFlag & 0XFF);
		return b;
	}
}
