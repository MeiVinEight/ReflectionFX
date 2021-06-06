package org.mve.asm;

import org.mve.asm.attribute.AttributeWriter;

import java.util.Arrays;

public class FieldWriter
{
	private int accessFlag;
	private String name;
	private String desc;
	private AttributeWriter[] attributes = new AttributeWriter[0];

	public FieldWriter set(int accessFlag, String name, String desc)
	{
		this.accessFlag = accessFlag;
		this.name = name;
		this.desc = desc;
		return this;
	}

	public int getAccessFlag()
	{
		return accessFlag;
	}

	public void setAccessFlag(int accessFlag)
	{
		this.accessFlag = accessFlag;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public void addAttribute(AttributeWriter writer)
	{
		int i = this.attributes.length;
		this.attributes = Arrays.copyOf(this.attributes, i+1);
		this.attributes[i] = writer;
	}

	public AttributeWriter[] getAttributes()
	{
		return Arrays.copyOf(this.attributes, this.attributes.length);
	}
}
