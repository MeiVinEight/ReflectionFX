package org.mve.asm.file.attribute.element;

import org.mve.asm.file.attribute.annotation.Annotation;

public class ElementAnnotationValue extends ElementValue
{
	public Annotation annotation;

	@Override
	public int length()
	{
		return 1 + this.annotation.length();
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		b[0] = (byte) type;
		System.arraycopy(this.annotation.toByteArray(), 0, b, 1, len-1);
		return b;
	}
}
