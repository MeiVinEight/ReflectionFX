package org.mve.util.asm.file;

import java.util.Objects;

public class AttributeRuntimeInvisibleTypeAnnotations extends Attribute
{
	private short annotationCount;
	private StructTypeAnnotation[] annotations = new StructTypeAnnotation[0];

	public AttributeRuntimeInvisibleTypeAnnotations(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getAnnotationCount()
	{
		return annotationCount;
	}

	public void addTypeAnnotation(StructTypeAnnotation annotation)
	{
		StructTypeAnnotation[] arr = new StructTypeAnnotation[this.annotationCount+1];
		System.arraycopy(this.annotations, 0, arr, 0, this.annotationCount);
		arr[this.annotationCount] = Objects.requireNonNull(annotation);
		this.annotations = arr;
		this.annotationCount++;
	}

	public void setTypeAnnotation(int index, StructTypeAnnotation annotation)
	{
		this.annotations[index] = Objects.requireNonNull(annotation);
	}

	public StructTypeAnnotation getTypeAnnotation(int index)
	{
		return this.annotations[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS;
	}

	@Override
	public int getLength()
	{
		int len = 2;
		for (StructTypeAnnotation s : this.annotations) len += s.getLength();
		return len;
	}
}
