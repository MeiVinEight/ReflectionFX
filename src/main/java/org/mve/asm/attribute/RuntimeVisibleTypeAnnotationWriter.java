package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeRuntimeVisibleTypeAnnotations;
import org.mve.asm.file.constant.ConstantArray;

public class RuntimeVisibleTypeAnnotationWriter extends TypeAnnotationArray<RuntimeVisibleTypeAnnotationWriter> implements AttributeWriter
{
	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeRuntimeVisibleTypeAnnotations attribute = new AttributeRuntimeVisibleTypeAnnotations();
		attribute.name = ConstantPoolFinder.findUTF8(pool, attribute.type().getName());
		attribute.annotation = this.array(pool);
		return attribute;
	}
}
