package org.mve.asm.file.attribute;

import org.mve.asm.file.attribute.inner.InnerClass;

import java.util.Arrays;

public class AttributeInnerClasses extends Attribute
{
	public InnerClass[] inner = new InnerClass[0];

	public void inner(InnerClass inner)
	{
		this.inner = Arrays.copyOf(this.inner, this.inner.length + 1);
		this.inner[this.inner.length - 1] = inner;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.INNER_CLASSES;
	}

	@Override
	public int length()
	{
		return 8 + (8 * this.inner.length);
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
		b[index++] = (byte) ((this.inner.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.inner.length & 0XFF);
		for (InnerClass s : this.inner)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 8);
			index+=8;
		}
		return b;
	}
}
