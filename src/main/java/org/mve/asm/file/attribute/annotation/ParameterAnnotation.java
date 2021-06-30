package org.mve.asm.file.attribute.annotation;

import java.util.Arrays;

public class ParameterAnnotation
{
	public Annotation[] annotation = new Annotation[0];

	public void annotation(Annotation annotation)
	{
		this.annotation = Arrays.copyOf(this.annotation, this.annotation.length + 1);
		this.annotation[this.annotation.length - 1] = annotation;
	}

	public int length()
	{
		int len = 2;
		for (Annotation s : this.annotation) len += s.length();
		return len;
	}

	public byte[] toByteArray()
	{
		int len = this.length();
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = (byte) ((this.annotation.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.annotation.length & 0XFF);
		for (Annotation s : this.annotation)
		{
			int l = s.length();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
