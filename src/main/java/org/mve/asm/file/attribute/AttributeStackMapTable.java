package org.mve.asm.file.attribute;

import org.mve.asm.file.attribute.stack.StackMapFrame;

import java.util.Arrays;

public class AttributeStackMapTable extends Attribute
{
	public StackMapFrame[] frame = new StackMapFrame[0];

	public void frame(StackMapFrame frame)
	{
		this.frame = Arrays.copyOf(this.frame, this.frame.length+1);
		this.frame[this.frame.length-1] = frame;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.STACK_MAP_TABLE;
	}

	@Override
	public int length()
	{
		int len = 8;
		for (StackMapFrame frame : this.frame) len += frame.length();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((name >>> 8) & 0XFF);
		b[index++] = (byte) (name & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) ((this.frame.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.frame.length & 0XFF);
		for (StackMapFrame s : this.frame)
		{
			int l = s.length();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
