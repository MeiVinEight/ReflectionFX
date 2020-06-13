package org.mve.util.asm.file;

import org.mve.util.Binary;

public class StructInnerClass implements Binary
{
	private short innerClassInfoIndex;
	private short outerClassInfoIndex;
	private short innerNameIndex;
	private short innerClassAccessFlag;

	public void setInnerClassInfoIndex(short innerClassInfoIndex)
	{
		this.innerClassInfoIndex = innerClassInfoIndex;
	}

	public void setOuterClassInfoIndex(short outerClassInfoIndex)
	{
		this.outerClassInfoIndex = outerClassInfoIndex;
	}

	public void setInnerNameIndex(short innerNameIndex)
	{
		this.innerNameIndex = innerNameIndex;
	}

	public void setInnerClassAccessFlag(short innerClassAccessFlag)
	{
		this.innerClassAccessFlag = innerClassAccessFlag;
	}

	public short getInnerClassInfoIndex()
	{
		return innerClassInfoIndex;
	}

	public short getOuterClassInfoIndex()
	{
		return outerClassInfoIndex;
	}

	public short getInnerNameIndex()
	{
		return innerNameIndex;
	}

	public short getInnerClassAccessFlag()
	{
		return innerClassAccessFlag;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[8];
		b[0] = (byte) ((this.innerClassInfoIndex >>> 8) & 0XFF);
		b[1] = (byte) (this.innerClassInfoIndex & 0XFF);
		b[2] = (byte) ((this.outerClassInfoIndex >>> 8) & 0XFF);
		b[3] = (byte) (this.outerClassInfoIndex & 0XFF);
		b[4] = (byte) ((this.innerNameIndex >>> 8) & 0XFF);
		b[5] = (byte) (this.innerNameIndex & 0XFF);
		b[6] = (byte) ((this.innerClassAccessFlag >>> 8) & 0XFF);
		b[7] = (byte) (this.innerClassAccessFlag & 0XFF);
		return b;
	}
}
