package org.mve.util.asm.file;

import org.mve.util.Binary;

import java.util.Objects;

public class StructParameterAnnotation implements Binary
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

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		int index = 0;
		byte[] b = new byte[len];
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
