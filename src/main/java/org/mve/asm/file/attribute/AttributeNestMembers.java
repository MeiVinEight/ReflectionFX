package org.mve.asm.file.attribute;

import java.util.Arrays;

public class AttributeNestMembers extends Attribute
{
	public int[] classes = new int[0];

	public void classes(int c)
	{
		this.classes = Arrays.copyOf(this.classes, this.classes.length+1);
		this.classes[this.classes.length-1] = c;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.NEST_MEMBERS;
	}

	@Override
	public int length()
	{
		return 8 + (2 * this.classes.length);
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
		b[index++] = (byte) ((this.classes.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.classes.length & 0XFF);
		for (int s : this.classes)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
