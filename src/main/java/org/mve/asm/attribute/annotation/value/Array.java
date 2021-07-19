package org.mve.asm.attribute.annotation.value;

import org.mve.asm.attribute.annotation.NameAndValue;

import java.util.Arrays;

public class Array
{
	public NameAndValue[] value;

	public Array(NameAndValue... value)
	{
		this.value = value;
	}

	public Array()
	{
		this.value = new NameAndValue[0];
	}

	public Array value(String name, Object value)
	{
		return this.value(new NameAndValue(name, value));
	}

	public Array value(NameAndValue value)
	{
		this.value = Arrays.copyOf(this.value, this.value.length+1);
		this.value[this.value.length-1] = value;
		return this;
	}
}
