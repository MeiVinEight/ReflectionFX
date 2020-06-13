package org.mve.util.asm.file;

import org.mve.util.Binary;

import java.util.Objects;

public class StructAnnotation implements Binary
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

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((this.typeIndex >>> 8) & 0XFF);
		b[index++] = (byte) (this.typeIndex & 0XFF);
		b[index++] = (byte) ((this.elementValuePairsCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.elementValuePairsCount & 0XFF);
		for (StructElementValuePairs s : this.elementValuePairs)
		{
			int l = s.getLength();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
