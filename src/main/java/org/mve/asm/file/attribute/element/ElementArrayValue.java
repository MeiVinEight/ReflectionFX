package org.mve.asm.file.attribute.element;

import java.util.Arrays;

public class ElementArrayValue extends ElementValue
{
	public ElementValue[] value = new ElementValue[0];

	public void value(ElementValue value)
	{
		this.value = Arrays.copyOf(this.value, this.value.length + 1);
		this.value[this.value.length - 1] = value;
	}

	@Override
	public int length()
	{
		int len = 3;
		for (ElementValue e : this.value) len += e.length();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = (byte) type;
		b[index++] = (byte) ((this.value.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.value.length & 0XFF);
		for (ElementValue value : this.value)
		{
			int l = value.length();
			System.arraycopy(value.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
