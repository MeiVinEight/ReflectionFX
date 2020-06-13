package org.mve.util.asm.file;

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
		return 8 + (2 * this.classCount);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[index++] = (byte) (this.getAttributeNameIndex() & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) ((this.classCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.classCount & 0XFF);
		for (short s : this.classes)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
