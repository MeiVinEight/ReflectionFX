package org.mve.asm.file;

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
		int len = 7;
		for (StructParameterAnnotation s : this.parameterAnnotations) len += s.getLength();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[index++] = (byte) (this.getAttributeNameIndex() & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = parameterCount;
		for (StructParameterAnnotation s : this.parameterAnnotations)
		{
			int l = s.getLength();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
