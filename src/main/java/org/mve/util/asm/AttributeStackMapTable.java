package org.mve.util.asm;

public class AttributeStackMapTable extends Attribute
{
	private short numberOfEntries;
	private StackMapFrame[] entries = new StackMapFrame[0];

	public AttributeStackMapTable(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getNumberOfEntries()
	{
		return numberOfEntries;
	}

	public void addStackMapFrame(StackMapFrame frame)
	{
		StackMapFrame[] frames = new StackMapFrame[++this.numberOfEntries];
		System.arraycopy(this.entries, 0, frames, 0, this.entries.length);
		frames[this.entries.length] = frame;
		this.entries = frames;
	}

	public void setStackMapFrame(int index, StackMapFrame frame)
	{
		this.entries[index] = frame;
	}

	public StackMapFrame getStackMapFrame(int index)
	{
		return this.entries[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.STACK_MAP_TABLE;
	}

	@Override
	public int getLength()
	{
		int len = 2;
		for (StackMapFrame frame : this.entries) len += frame.getLength();
		return len;
	}
}
