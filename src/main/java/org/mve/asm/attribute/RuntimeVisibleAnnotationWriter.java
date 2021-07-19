package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.annotation.Annotation;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeRuntimeVisibleAnnotations;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;

public class RuntimeVisibleAnnotationWriter extends AnnotationArray<RuntimeVisibleAnnotationWriter> implements AttributeWriter
{
	public RuntimeVisibleAnnotationWriter(Annotation[] annotation)
	{
		super(annotation);
	}

	public RuntimeVisibleAnnotationWriter()
	{
		super(new Annotation[0]);
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeRuntimeVisibleAnnotations attr = new AttributeRuntimeVisibleAnnotations();
		attr.name = ConstantPoolFinder.findUTF8(pool, AttributeType.RUNTIME_VISIBLE_ANNOTATIONS.getName());
		attr.annotation = this.array(pool);
		return attr;
	}
}
