package org.mve.asm.attribute;

import org.mve.asm.attribute.annotation.ParameterAnnotation;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public abstract class ParameterAnnotationArray<T extends ParameterAnnotationArray<T>>
{
	public ParameterAnnotation[] annotation = new ParameterAnnotation[0];

	@SuppressWarnings("unchecked")
	public T annotation(ParameterAnnotation annotation)
	{
		this.annotation = Arrays.copyOf(this.annotation, this.annotation.length+1);
		this.annotation[this.annotation.length-1] = annotation;
		return (T) this;
	}

	public org.mve.asm.file.attribute.annotation.ParameterAnnotation[] array(ConstantArray array)
	{
		org.mve.asm.file.attribute.annotation.ParameterAnnotation[] value = new org.mve.asm.file.attribute.annotation.ParameterAnnotation[this.annotation.length];
		for (int i = 0; i < this.annotation.length; i++)
		{
			org.mve.asm.file.attribute.annotation.ParameterAnnotation annotation = new org.mve.asm.file.attribute.annotation.ParameterAnnotation();
			annotation.annotation = this.annotation[i].array(array);
			value[i] = annotation;
		}
		return value;
	}
}
