package org.mve.asm.attribute.annotation;

import org.mve.asm.attribute.AnnotationArray;
import org.mve.asm.file.constant.ConstantArray;

public class ParameterAnnotation extends AnnotationArray<ParameterAnnotation>
{
	public ParameterAnnotation(Annotation... annotation)
	{
		super(annotation);
	}

	public ParameterAnnotation()
	{
		super(new Annotation[0]);
	}

	public org.mve.asm.file.attribute.annotation.ParameterAnnotation annotation(ConstantArray array)
	{
		org.mve.asm.file.attribute.annotation.ParameterAnnotation annotation = new org.mve.asm.file.attribute.annotation.ParameterAnnotation();
		annotation.annotation = this.array(array);
		return annotation;
	}
}
