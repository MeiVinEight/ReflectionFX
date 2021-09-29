package org.mve.asm.file.attribute;

import org.mve.asm.file.attribute.record.RecordComponent;

import java.util.Arrays;

public class AttributeRecord extends Attribute
{
	public RecordComponent[] component = new RecordComponent[0];

	public void component(RecordComponent component)
	{
		this.component = Arrays.copyOf(this.component, this.component.length+1);
		this.component[this.component.length-1] = component;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.RECORD;
	}

	@Override
	public int length()
	{
		int l = 8;
		for (RecordComponent component : this.component)
		{
			l += component.length();
		}
		return l;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		int i = 0;
		byte[] array = new byte[len];
		array[i++] = (byte) ((this.name >>> 8) & 0xFF);
		array[i++] = (byte) (this.name & 0xFF);
		len -= 6;
		array[i++] = (byte) (len >>> 24 & 0xFF);
		array[i++] = (byte) (len >>> 16 & 0xFF);
		array[i++] = (byte) (len >>> 8 & 0xFF);
		array[i++] = (byte) (len & 0xFF);
		array[i++] = (byte) ((this.component.length >>> 8) & 0xFF);
		array[i++] = (byte) (this.component.length & 0xFF);
		for (RecordComponent component : this.component)
		{
			int cl = component.length();
			System.arraycopy(component.array(), 0, array, i, cl);
			i += cl;
		}
		return array;
	}
}
