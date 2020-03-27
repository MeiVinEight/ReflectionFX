package org.mve.util.asm.file;

import java.util.Objects;

public class StructAnnotation
{
	private short typeIndex;
	private short elementValuePairsCount;
	private StructElementValuePairs[] elementValuePairs = new StructElementValuePairs[0];

	public short getTypeIndex()
	{
		return typeIndex;
	}

	public void setTypeIndex(short typeIndex)
	{
		this.typeIndex = typeIndex;
	}

	public short getElementValuePairsCount()
	{
		return elementValuePairsCount;
	}

	public void addElementValuePairs(StructElementValuePairs pairs)
	{
		StructElementValuePairs[] arr = new StructElementValuePairs[this.elementValuePairsCount+1];
		System.arraycopy(this.elementValuePairs, 0, arr, 0, this.elementValuePairsCount);
		arr[this.elementValuePairsCount] = Objects.requireNonNull(pairs);
		this.elementValuePairs = arr;
		this.elementValuePairsCount++;
	}

	public int getLength()
	{
		int len = 4;
		for (StructElementValuePairs s : this.elementValuePairs) len += s.getLength();
		return len;
	}
}
