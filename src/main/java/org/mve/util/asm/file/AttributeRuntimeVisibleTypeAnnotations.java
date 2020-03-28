package org.mve.util.asm.file;

import java.util.Objects;

public class AttributeRuntimeVisibleTypeAnnotations extends Attribute
{
	private short annotationCount;
	private StructTypeAnnotation[] annotations = new StructTypeAnnotation[0];

	public AttributeRuntimeVisibleTypeAnnotations(short attributeNameIndex)
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
		return AttributeType.RUNTIME_VISIBLE_TYPE_ANNOTATIONS;
	}

	@Override
	public int getLength()
	{
		int len = 8;
		for (StructTypeAnnotation s : this.annotations) len += s.getLength();
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
		for (StructTypeAnnotation s : this.annotations)
		{
			int l = s.getLength();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
