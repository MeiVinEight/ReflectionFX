package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.constant.ConstantValue;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeConstantValue;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;

public class ConstantValueWriter implements AttributeWriter
{
	public Object value;

	public ConstantValueWriter(Object value)
	{
		this.value = value;
	}

	public ConstantValueWriter()
	{
	}

	public ConstantValueWriter value(Object value)
	{
		this.value = value;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeConstantValue attribute = new AttributeConstantValue();
		attribute.name = ConstantPoolFinder.findUTF8(pool, AttributeType.CONSTANT_VALUE.getName());
		attribute.value = ConstantValue.constant(pool, this.value);
		return attribute;
	}
}
