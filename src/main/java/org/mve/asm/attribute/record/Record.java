package org.mve.asm.attribute.record;

import org.mve.asm.attribute.AttributeWriter;

import java.util.Arrays;

public class Record
{
	public String name;
	public String type;
	public AttributeWriter[] attribute;

	public Record(String name, String type, AttributeWriter... attribute)
	{
		this.name = name;
		this.type = type;
		this.attribute = attribute;
	}

	public Record()
	{
	}

	public Record name(String name)
	{
		this.name = name;
		return this;
	}

	public Record type(String type)
	{
		this.type = type;
		return this;
	}

	public Record attribute(AttributeWriter attribute)
	{
		this.attribute = Arrays.copyOf(this.attribute,  this.attribute.length+1);
		this.attribute[this.attribute.length-1] = attribute;
		return this;
	}
}
