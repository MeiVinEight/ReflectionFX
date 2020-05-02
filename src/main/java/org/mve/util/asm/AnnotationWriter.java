package org.mve.util.asm;

import org.mve.util.asm.file.ConstantPool;
import org.mve.util.asm.file.StructAnnotation;

public class AnnotationWriter
{
	private String type;

	public AnnotationWriter set(String type)
	{
		this.type = type;
		return this;
	}

	public StructAnnotation get(ConstantPool pool)
	{
		StructAnnotation annotation = new StructAnnotation();
		annotation.setTypeIndex((short) ConstantPoolFinder.findUTF8(pool, type));
		return annotation;
	}
}
