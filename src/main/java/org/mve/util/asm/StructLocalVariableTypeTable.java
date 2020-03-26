package org.mve.util.asm;

public class StructLocalVariableTypeTable
{
	private short startPc;
	private short length;
	private short nameIndex;
	private short SignatureIndex;
	private short index;

	public short getStartPc()
	{
		return startPc;
	}

	public void setStartPc(short startPc)
	{
		this.startPc = startPc;
	}

	public short getLength()
	{
		return length;
	}

	public void setLength(short length)
	{
		this.length = length;
	}

	public short getNameIndex()
	{
		return nameIndex;
	}

	public void setNameIndex(short nameIndex)
	{
		this.nameIndex = nameIndex;
	}

	public short getSignatureIndex()
	{
		return SignatureIndex;
	}

	public void setSignatureIndex(short signatureIndex)
	{
		SignatureIndex = signatureIndex;
	}

	public short getIndex()
	{
		return index;
	}

	public void setIndex(short index)
	{
		this.index = index;
	}
}
