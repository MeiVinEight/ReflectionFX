package org.mve.util.asm.file;

import java.util.Objects;

public class AttributeRuntimeVisibleAnnotations extends Attribute
{
	private short annotationCount;
	private StructAnnotation[] annotations = new StructAnnotation[0];

	public AttributeRuntimeVisibleAnnotations(short attributeNameIndex)
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
		return AttributeType.RUNTIME_VISIBLE_ANNOTATIONS;
	}

	@Override
	public int getLength()
	{
		int len = 8;
		for (StructAnnotation a : this.annotations) len += a.getLength();
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
		b[index++] = (byte) ((this.annotationCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.annotationCount & 0XFF);
		for (StructAnnotation s : this.annotations)
		{
			int l = s.getLength();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
