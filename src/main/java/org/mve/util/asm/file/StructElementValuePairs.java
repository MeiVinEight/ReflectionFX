package org.mve.util.asm.file;

import org.mve.util.Binary;

public class StructElementValuePairs implements Binary
{
	private short elementNameIndex;
	private ElementValue elementValue;

	public void setElementNameIndex(short elementNameIndex)
	{
		this.elementNameIndex = elementNameIndex;
	}

	public ElementValue getElementValue()
	{
		return elementValue;
	}

	public void setElementValue(ElementValue elementValue)
	{
		this.elementValue = elementValue;
	}

	public short getElementNameIndex()
	{
		return elementNameIndex;
	}

	public int getLength()
	{
		return 2 + this.elementValue.getLength();
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		b[0] = (byte) ((this.elementNameIndex >>> 8) & 0XFF);
		b[1] = (byte) (this.elementNameIndex & 0XFF);
		System.arraycopy(this.elementValue.toByteArray(), 0, b, 2, len-2);
		return b;
	}
}
