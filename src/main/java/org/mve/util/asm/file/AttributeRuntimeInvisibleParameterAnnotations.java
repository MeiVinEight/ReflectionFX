package org.mve.util.asm.file;

import java.util.Objects;

public class AttributeRuntimeInvisibleParameterAnnotations extends Attribute
{
	private byte parameterCount;
	private StructParameterAnnotation[] parameterAnnotations = new StructParameterAnnotation[0];

	public AttributeRuntimeInvisibleParameterAnnotations(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public byte getParameterCount()
	{
		return parameterCount;
	}

	public void addParameterAnnotation(StructParameterAnnotation annotation)
	{
		StructParameterAnnotation[] arr = new StructParameterAnnotation[this.parameterCount+1];
		System.arraycopy(this.parameterAnnotations, 0, arr, 0, this.parameterCount);
		arr[this.parameterCount] = Objects.requireNonNull(annotation);
		this.parameterAnnotations = arr;
		this.parameterCount++;
	}

	public void setParameterAnnotation(int index, StructParameterAnnotation annotation)
	{
		this.parameterAnnotations[index] = Objects.requireNonNull(annotation);
	}

	public StructParameterAnnotation getParameterAnnotation(int index)
	{
		return this.parameterAnnotations[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS;
	}

	@Override
	public int getLength()
	{
		int len = 1;
		for (StructParameterAnnotation s : this.parameterAnnotations) len += s.getLength();
		return len;
	}
}
