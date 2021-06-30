package org.mve.asm;

import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.annotation.Annotation;

public class AnnotationWriter
{
	private String type;

	public AnnotationWriter set(String type)
	{
		this.type = type;
		return this;
	}

	public Annotation get(ConstantArray pool)
	{
		Annotation annotation = new Annotation();
		annotation.type = ConstantPoolFinder.findUTF8(pool, type);
		return annotation;
	}
}
