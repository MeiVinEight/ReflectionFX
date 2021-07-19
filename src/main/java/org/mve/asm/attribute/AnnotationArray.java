package org.mve.asm.attribute;

import org.mve.asm.attribute.annotation.Annotation;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public abstract class AnnotationArray<T extends AnnotationArray<T>>
{
	public Annotation[] annotation;

	public AnnotationArray(Annotation[] annotation)
	{
		this.annotation = annotation;
	}

	@SuppressWarnings("unchecked")
	public T annotation(Annotation annotation)
	{
		this.annotation = Arrays.copyOf(this.annotation, this.annotation.length+1);
		this.annotation[this.annotation.length-1] = annotation;
		return (T) this;
	}

	public org.mve.asm.file.attribute.annotation.Annotation[] array(ConstantArray constant)
	{
		org.mve.asm.file.attribute.annotation.Annotation[] array = new org.mve.asm.file.attribute.annotation.Annotation[this.annotation.length];
		for (int i = 0; i < this.annotation.length; i++)
		{
			array[i] = this.annotation[i].annotation(constant);
		}
		return array;
	}
}
