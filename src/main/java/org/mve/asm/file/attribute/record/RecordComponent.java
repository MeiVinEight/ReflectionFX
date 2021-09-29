package org.mve.asm.file.attribute.record;

import org.mve.asm.file.Class;
import org.mve.asm.file.attribute.Attribute;
import org.mve.io.RandomAccessByteArray;

import java.util.Arrays;

public class RecordComponent
{
	public int name;
	public int type;
	public Attribute[] attribute = new Attribute[0];

	public void attribute(Attribute attribute)
	{
		this.attribute = Arrays.copyOf(this.attribute, this.attribute.length + 1);
		this.attribute[this.attribute.length - 1] = attribute;
	}

	public int length()
	{
		int l = 6;
		for (Attribute attribute : this.attribute)
		{
			l += attribute.length();
		}
		return l;
	}

	public byte[] array()
	{
		int len = this.length();
		int i = 0;
		byte[] array = new byte[len];
		array[i++] = (byte) ((this.name >>> 8) & 0xFF);
		array[i++] = (byte) (this.name & 0xFF);
		array[i++] = (byte) ((this.type >>> 8) & 0xFF);
		array[i++] = (byte) (this.type & 0xFF);
		array[i++] = (byte) ((this.attribute.length >>> 8) & 0xFF);
		array[i++] = (byte) (this.attribute.length & 0xFF);
		for (Attribute attribute : this.attribute)
		{
			int al = attribute.length();
			System.arraycopy(attribute.toByteArray(), 0, array, i, al);
			i += al;
		}
		return array;
	}

	public static RecordComponent read(Class file, RandomAccessByteArray din)
	{
		RecordComponent component = new RecordComponent();
		component.name = din.readUnsignedShort();
		component.type = din.readUnsignedShort();
		int count = din.readUnsignedShort();
		for (int i = 0; i < count; i++)
		{
			component.attribute(Attribute.read(file, din));
		}
		return component;
	}
}
