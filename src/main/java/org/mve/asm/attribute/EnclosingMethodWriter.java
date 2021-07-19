package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeEnclosingMethod;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;

public class EnclosingMethodWriter implements AttributeWriter
{
	public String type;
	public String name;
	public String sign;

	public EnclosingMethodWriter(String type, String name, String sign)
	{
		this.type = type;
		this.name = name;
		this.sign = sign;
	}

	public EnclosingMethodWriter()
	{
	}

	public EnclosingMethodWriter type(String type)
	{
		this.type = type;
		return this;
	}

	public EnclosingMethodWriter name(String name)
	{
		this.name = name;
		return this;
	}

	public EnclosingMethodWriter sign(String sign)
	{
		this.sign = sign;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeEnclosingMethod attribute = new AttributeEnclosingMethod();
		attribute.name = ConstantPoolFinder.findUTF8(pool, AttributeType.ENCLOSING_METHOD.getName());
		attribute.clazz = ConstantPoolFinder.findClass(pool, this.type);
		attribute.method = ConstantPoolFinder.findNameAndType(pool, this.name, this.sign);
		return attribute;
	}
}
