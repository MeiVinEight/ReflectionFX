package org.mve.util.asm.file;

public class StructInnerClass
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
}
