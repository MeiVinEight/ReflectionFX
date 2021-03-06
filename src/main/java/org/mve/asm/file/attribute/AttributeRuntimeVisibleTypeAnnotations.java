package org.mve.asm.file.attribute;

import org.mve.asm.file.attribute.annotation.TypeAnnotation;

import java.util.Arrays;

public class AttributeRuntimeVisibleTypeAnnotations extends Attribute
{
	public TypeAnnotation[] annotation = new TypeAnnotation[0];

	public void annotation(TypeAnnotation annotation)
	{
		this.annotation = Arrays.copyOf(this.annotation, this.annotation.length + 1);
		this.annotation[this.annotation.length-1] = annotation;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.RUNTIME_VISIBLE_TYPE_ANNOTATIONS;
	}

	@Override
	public int length()
	{
		int len = 8;
		for (TypeAnnotation s : this.annotation) len += s.length();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((name >>> 8) & 0XFF);
		b[index++] = (byte) (name & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) ((this.annotation.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.annotation.length & 0XFF);
		for (TypeAnnotation s : this.annotation)
		{
			int l = s.length();
			System.arraycopy(s.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
