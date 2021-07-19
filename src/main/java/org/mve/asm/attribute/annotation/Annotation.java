package org.mve.asm.attribute.annotation;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.element.ElementNameAndValue;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class Annotation
{
	public String type;
	public NameAndValue[] value;

	public Annotation(String type, NameAndValue... value)
	{
		this.type = type;
		this.value = value;
	}

	public Annotation()
	{
		this(null);
	}

	public Annotation type(String type)
	{
		this.type = type;
		return this;
	}

	public Annotation value(String name, Object value)
	{
		return this.value(new NameAndValue(name, value));
	}

	public Annotation value(NameAndValue value)
	{
		this.value = Arrays.copyOf(this.value, this.value.length+1);
		this.value[this.value.length-1] = value;
		return this;
	}

	public org.mve.asm.file.attribute.annotation.Annotation annotation(ConstantArray pool)
	{
		org.mve.asm.file.attribute.annotation.Annotation annotation = new org.mve.asm.file.attribute.annotation.Annotation();
		annotation.type = ConstantPoolFinder.findUTF8(pool, type);
		for (NameAndValue nameAndValue : this.value)
		{
			String name = nameAndValue.name;
			Object value = nameAndValue.value;

			ElementNameAndValue element = new ElementNameAndValue();
			element.name = ConstantPoolFinder.findUTF8(pool, name);
			element.value = NameAndValue.value(pool, value);
			annotation.element(element);
		}
		return annotation;
	}
}
