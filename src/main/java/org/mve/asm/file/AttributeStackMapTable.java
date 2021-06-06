package org.mve.asm.file;

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
		int len = 8;
		for (StackMapFrame frame : this.entries) len += frame.getLength();
		return len;
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
		b[index++] = (byte) ((this.numberOfEntries >>> 8) & 0XFF);
		b[index++] = (byte) (this.numberOfEntries & 0XFF);
		for (StackMapFrame s : this.entries)
		{
			int l = s.getLength();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
