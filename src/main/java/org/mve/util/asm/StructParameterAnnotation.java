package org.mve.util.asm;

import java.util.Objects;

public class StructParameterAnnotation
{
	private short annotationCount;
	private StructAnnotation[] annotations = new StructAnnotation[0];

	public short getAnnotationCount()
	{
		return annotationCount;
	}

	public void addAnnotation(StructAnnotation annotation)
	{
		StructAnnotation[] arr = new StructAnnotation[this.annotationCount+1];
		System.arraycopy(this.annotations, 0, arr, 0, this.annotationCount);
		arr[this.annotationCount] = Objects.requireNonNull(annotation);
		this.annotations = arr;
		this.annotationCount++;
	}

	public void setAnnotation(int index, StructAnnotation annotation)
	{
		this.annotations[index] = annotation;
	}

	public StructAnnotation getAnnotation(int index)
	{
		return this.annotations[index];
	}

	public int getLength()
	{
		int len = 2;
		for (StructAnnotation s : this.annotations) len += s.getLength();
		return len;
	}
}
