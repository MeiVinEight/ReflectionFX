package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeUnknown;
import org.mve.asm.file.constant.ConstantArray;

public class UnknownWriter implements AttributeWriter
{
	public String name;
	public byte[] data;

	public UnknownWriter(String name, byte[] data)
	{
		this.name = name;
		this.data = data;
	}

	public UnknownWriter name(String name)
	{
		this.name = name;
		return this;
	}

	public UnknownWriter data(byte[] data)
	{
		this.data = data;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeUnknown attribute = new AttributeUnknown();
		attribute.name = ConstantPoolFinder.findUTF8(pool, this.name);
		attribute.code = this.data;
		return attribute;
	}
}
