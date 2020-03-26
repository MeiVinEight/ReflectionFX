package org.mve.util.asm;

public class AttributeExceptions extends Attribute
{
	private short exceptionCount;
	private short[] exceptions = new short[0];

	public AttributeExceptions(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public void addException(short cpIndex)
	{
		short[] arr = new short[++this.exceptionCount];
		System.arraycopy(this.exceptions, 0, arr, 0, this.exceptions.length);
		arr[this.exceptions.length] = cpIndex;
		this.exceptions = arr;
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.EXCEPTIONS;
	}

	@Override
	public int getLength()
	{
		return 2 + (2 * this.exceptionCount);
	}
}
