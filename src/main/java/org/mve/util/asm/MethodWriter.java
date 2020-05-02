package org.mve.util.asm;

import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.attribute.SignatureWriter;
import org.mve.util.asm.attribute.AttributeWriter;

import java.util.Arrays;

public class MethodWriter
{
	private int accessFlag;
	private String name;
	private String desc;
	private AttributeWriter[] attributes = new AttributeWriter[0];

	public MethodWriter set(int accessFlag, String name, String desc)
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

	public MethodWriter addAttribute(AttributeWriter writer)
	{
		int i = this.attributes.length;
		this.attributes = Arrays.copyOf(this.attributes, i+1);
		this.attributes[i] = writer;
		return this;
	}

	public AttributeWriter[] getAttributes()
	{
		return Arrays.copyOf(this.attributes, this.attributes.length);
	}

	public CodeWriter addCode()
	{
		CodeWriter writer = new CodeWriter();
		this.addAttribute(writer);
		return writer;
	}

	public SignatureWriter addSignature(String signature)
	{
		if (signature == null) return null;
		SignatureWriter writer = new SignatureWriter(signature);
		this.addAttribute(writer);
		return writer;
	}
}
