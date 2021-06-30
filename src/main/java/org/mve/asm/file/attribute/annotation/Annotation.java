package org.mve.asm.file.attribute.annotation;

import org.mve.asm.file.Class;
import org.mve.asm.file.attribute.element.ElementNameAndValue;
import org.mve.asm.file.attribute.element.ElementValue;
import org.mve.io.RandomAccessByteArray;

import java.util.Arrays;

public class Annotation
{
	public int type;
	public ElementNameAndValue[] element = new ElementNameAndValue[0];

	public void element(ElementNameAndValue element)
	{
		this.element = Arrays.copyOf(this.element, this.element.length + 1);
		this.element[this.element.length - 1] = element;
	}

	public int length()
	{
		int len = 4;
		for (ElementNameAndValue s : this.element) len += s.length();
		return len;
	}

	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		int i = 0;
		b[i++] = (byte) ((this.type >>> 8) & 0XFF);
		b[i++] = (byte) (this.type & 0XFF);
		b[i++] = (byte) ((this.element.length >>> 8) & 0XFF);
		b[i++] = (byte) (this.element.length & 0XFF);
		for (ElementNameAndValue s : this.element)
		{
			int l = s.length();
			System.arraycopy(s.toByteArray(), 0, b, i, l);
			i+=l;
		}
		return b;
	}

	public static Annotation read(Class file, RandomAccessByteArray input)
	{
		Annotation annotation = new Annotation();
		annotation.type = input.readUnsignedShort();
		int count = input.readUnsignedShort();
		for (int i = 0; i < count; i++)
		{
			ElementNameAndValue element = new ElementNameAndValue();
			element.name = input.readUnsignedShort();
			element.value = ElementValue.read(file, input);
			annotation.element(element);
		}
		return annotation;
	}
}
