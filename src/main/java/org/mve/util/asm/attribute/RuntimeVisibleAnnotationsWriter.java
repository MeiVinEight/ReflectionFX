package org.mve.util.asm.attribute;

import org.mve.util.asm.AnnotationWriter;
import org.mve.util.asm.ConstantPoolFinder;
import org.mve.util.asm.file.Attribute;
import org.mve.util.asm.file.AttributeRuntimeVisibleAnnotations;
import org.mve.util.asm.file.AttributeType;
import org.mve.util.asm.file.ConstantPool;

import java.util.Arrays;

public class RuntimeVisibleAnnotationsWriter implements AttributeWriter
{
	private AnnotationWriter[] annotations = new AnnotationWriter[0];

	public RuntimeVisibleAnnotationsWriter addAnnotation(AnnotationWriter writer)
	{
		int i = annotations.length;
		this.annotations = Arrays.copyOf(annotations, i+1);
		annotations[i] = writer;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantPool pool)
	{
		AttributeRuntimeVisibleAnnotations attr = new AttributeRuntimeVisibleAnnotations((short) ConstantPoolFinder.findUTF8(pool, AttributeType.RUNTIME_VISIBLE_ANNOTATIONS.getName()));
		for (AnnotationWriter writer : this.annotations) attr.addAnnotation(writer.get(pool));
		return attr;
	}
}
