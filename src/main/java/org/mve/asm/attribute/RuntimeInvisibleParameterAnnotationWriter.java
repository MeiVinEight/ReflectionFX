package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeRuntimeInvisibleParameterAnnotations;
import org.mve.asm.file.constant.ConstantArray;

public class RuntimeInvisibleParameterAnnotationWriter extends ParameterAnnotationArray<RuntimeVisibleParameterAnnotationWriter> implements AttributeWriter
{
	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeRuntimeInvisibleParameterAnnotations attribute = new AttributeRuntimeInvisibleParameterAnnotations();
		attribute.name = ConstantPoolFinder.findUTF8(pool, attribute.type().getName());
		attribute.annotation = this.array(pool);
		return attribute;
	}
}
