package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeRuntimeInvisibleTypeAnnotations;
import org.mve.asm.file.constant.ConstantArray;

public class RuntimeInvisibleTypeAnnotationWriter extends TypeAnnotationArray<RuntimeInvisibleTypeAnnotationWriter> implements AttributeWriter
{
	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeRuntimeInvisibleTypeAnnotations attribute = new AttributeRuntimeInvisibleTypeAnnotations();
		attribute.name = ConstantPoolFinder.findUTF8(pool, attribute.type().getName());
		attribute.annotation = this.array(pool);
		return attribute;
	}
}
