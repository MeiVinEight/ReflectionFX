package org.mve.util.asm;

public class AttributeNestMembers extends Attribute
{
	private short classCount;
	private short[] classes = new short[0];

	public AttributeNestMembers(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getClassCount()
	{
		return classCount;
	}

	public void addNestMember(short cpIndex)
	{
		short[] arr = new short[this.classCount+1];
		System.arraycopy(this.classes, 0, arr, 0, this.classCount);
		arr[this.classCount] = cpIndex;
		this.classes = arr;
		this.classCount++;
	}

	public void setNestMember(int index, short cpIndex)
	{
		this.classes[index] = cpIndex;
	}

	public short getNestHost(int index)
	{
		return this.classes[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.NEST_MEMBERS;
	}

	@Override
	public int getLength()
	{
		return 2 + (2 * this.classCount);
	}
}
