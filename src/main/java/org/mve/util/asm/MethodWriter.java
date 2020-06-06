package org.mve.util.asm;

import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.attribute.SignatureWriter;
import org.mve.util.asm.attribute.AttributeWriter;

import java.util.Arrays;

public class MethodWriter
{
	private final ClassWriter classWriter;
	private int accessFlag;
	private String name;
	private String type;
	private AttributeWriter[] attributes = new AttributeWriter[0];

	public MethodWriter(ClassWriter cw)
	{
		this.classWriter = cw;
	}

	public ClassWriter getClassWriter()
	{
		return classWriter;
	}

	public MethodWriter set(int accessFlag, String name, String desc)
	{
		this.accessFlag = accessFlag;
		this.name = name;
		this.type = desc;
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

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
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
		CodeWriter writer = new CodeWriter(this.classWriter);
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
