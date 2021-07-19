package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeRuntimeVisibleParameterAnnotations;
import org.mve.asm.file.constant.ConstantArray;

public class RuntimeVisibleParameterAnnotationWriter extends ParameterAnnotationArray<RuntimeVisibleParameterAnnotationWriter> implements AttributeWriter
{
	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeRuntimeVisibleParameterAnnotations attribute = new AttributeRuntimeVisibleParameterAnnotations();
		attribute.name = ConstantPoolFinder.findUTF8(pool, attribute.type().getName());
		attribute.annotation = this.array(pool);
		return attribute;
	}
}
