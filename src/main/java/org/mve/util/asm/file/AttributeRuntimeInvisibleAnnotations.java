package org.mve.util.asm.file;

import java.util.Objects;

public class AttributeRuntimeInvisibleAnnotations extends Attribute
{
	private short annotationCount;
	private StructAnnotation[] annotations = new StructAnnotation[0];

	public AttributeRuntimeInvisibleAnnotations(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

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
		this.annotations[index] = Objects.requireNonNull(annotation);
	}

	public StructAnnotation getAnnotation(int index)
	{
		return this.annotations[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.RUNTIME_INVISIBLE_ANNOTATIONS;
	}

	@Override
	public int getLength()
	{
		int len = 2;
		for (StructAnnotation a : this.annotations) len += a.getLength();
		return len;
	}
}
