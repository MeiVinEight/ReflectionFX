package org.mve.util.asm;

public class ElementValueArray extends ElementValue
{
	private short valueCount;
	private ElementValue[] values = new ElementValue[0];

	public ElementValueArray(byte type)
	{
		super(type);
	}

	public short getValueCount()
	{
		return valueCount;
	}

	public void addElementValue(ElementValue value)
	{
		ElementValue[] arr = new ElementValue[this.valueCount];
		System.arraycopy(this.values, 0, arr, 0, this.valueCount);
		arr[this.valueCount] = value;
		this.values = arr;
		this.valueCount++;
	}

	@Override
	public int getLength()
	{
		int len = 3;
		for (ElementValue e : this.values) len += e.getLength();
		return len;
	}
}
