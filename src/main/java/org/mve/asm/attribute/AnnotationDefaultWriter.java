package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.annotation.NameAndValue;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeAnnotationDefault;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;

public class AnnotationDefaultWriter implements AttributeWriter
{
	public Object value;

	public AnnotationDefaultWriter(Object value)
	{
		this.value = value;
	}

	public AnnotationDefaultWriter()
	{
	}

	public AnnotationDefaultWriter value(Object value)
	{
		this.value = value;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeAnnotationDefault attribute = new AttributeAnnotationDefault();
		attribute.name = ConstantPoolFinder.findUTF8(pool, AttributeType.ANNOTATION_DEFAULT.getName());
		attribute.value = NameAndValue.value(pool, this.value);
		return attribute;
	}
}
