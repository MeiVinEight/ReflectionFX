package org.mve.asm.file.attribute;

import org.mve.asm.file.attribute.element.ElementValue;

public class AttributeAnnotationDefault extends Attribute
{
	public ElementValue value;

	@Override
	public AttributeType type()
	{
		return AttributeType.ANNOTATION_DEFAULT;
	}

	@Override
	public int length()
	{
		return this.value.length() + 6;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		b[0] = (byte) ((this.name >>> 8) & 0xFF);
		b[1] = (byte) (this.name & 0xFF);
		len -= 6;
		b[2] = (byte) ((len >>> 24) & 0xFF);
		b[3] = (byte) ((len >>> 16) & 0xFF);
		b[4] = (byte) ((len >>> 8) & 0xFF);
		b[5] = (byte) (len & 0xFF);
		System.arraycopy(this.value.toByteArray(), 0, b, 6, len);
		return b;
	}
}
