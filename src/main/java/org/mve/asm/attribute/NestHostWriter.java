package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeNestHost;
import org.mve.asm.file.constant.ConstantArray;

public class NestHostWriter implements AttributeWriter
{
	public String name;

	public NestHostWriter(String name)
	{
		this.name = name;
	}

	public NestHostWriter()
	{
	}

	public NestHostWriter name(String name)
	{
		this.name = name;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeNestHost attribute = new AttributeNestHost();
		attribute.name = ConstantPoolFinder.findUTF8(pool, attribute.type().getName());
		attribute.host = ConstantPoolFinder.findClass(pool, this.name);
		return attribute;
	}
}
