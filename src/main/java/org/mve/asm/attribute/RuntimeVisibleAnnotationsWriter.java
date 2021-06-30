package org.mve.asm.attribute;

import org.mve.asm.AnnotationWriter;
import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeRuntimeVisibleAnnotations;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class RuntimeVisibleAnnotationsWriter implements AttributeWriter
{
	public AnnotationWriter[] annotations = new AnnotationWriter[0];

	public RuntimeVisibleAnnotationsWriter addAnnotation(AnnotationWriter writer)
	{
		int i = annotations.length;
		this.annotations = Arrays.copyOf(annotations, i+1);
		annotations[i] = writer;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeRuntimeVisibleAnnotations attr = new AttributeRuntimeVisibleAnnotations();
		attr.name = ConstantPoolFinder.findUTF8(pool, AttributeType.RUNTIME_VISIBLE_ANNOTATIONS.getName());
		for (AnnotationWriter writer : this.annotations) attr.annotation(writer.get(pool));
		return attr;
	}
}
