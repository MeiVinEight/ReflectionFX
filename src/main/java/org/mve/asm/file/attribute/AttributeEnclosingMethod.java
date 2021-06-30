package org.mve.asm.file.attribute;

public class AttributeEnclosingMethod extends Attribute
{
	public int clazz;
	public int method;

	@Override
	public AttributeType type()
	{
		return AttributeType.ENCLOSING_METHOD;
	}

	@Override
	public int length()
	{
		return 10;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[10];
		int index = 0;
		b[index++] = (byte) ((name >>> 8) & 0XFF);
		b[index++] = (byte) (name & 0XFF);
		b[index++] = 0;
		b[index++] = 0;
		b[index++] = 0;
		b[index++] = 4;
		b[index++] = (byte) ((this.clazz >>> 8) & 0XFF);
		b[index++] = (byte) (this.clazz & 0XFF);
		b[index++] = (byte) ((this.method >>> 8) & 0XFF);
		b[index] = (byte) (this.method & 0XFF);
		return b;
	}
}
