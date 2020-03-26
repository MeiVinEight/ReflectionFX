package org.mve.util.asm;

public class StructElementValuePairs
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
}
