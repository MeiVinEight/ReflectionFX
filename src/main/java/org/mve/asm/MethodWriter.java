package org.mve.asm;

import org.mve.asm.attribute.AttributeWriter;

import java.util.Arrays;

public class MethodWriter
{
	public int access;
	public String name;
	public String type;
	public AttributeWriter[] attribute = new AttributeWriter[0];

	public MethodWriter set(int accessFlag, String name, String desc)
	{
		this.access = accessFlag;
		this.name = name;
		this.type = desc;
		return this;
	}

	public MethodWriter access(int access)
	{
		this.access = access;
		return this;
	}

	public MethodWriter name(String name)
	{
		this.name = name;
		return this;
	}

	public MethodWriter type(String type)
	{
		this.type = type;
		return this;
	}

	public MethodWriter attribute(AttributeWriter writer)
	{
		int i = this.attribute.length;
		this.attribute = Arrays.copyOf(this.attribute, i+1);
		this.attribute[i] = writer;
		return this;
	}

	public AttributeWriter[] getAttribute()
	{
		return Arrays.copyOf(this.attribute, this.attribute.length);
	}
}
