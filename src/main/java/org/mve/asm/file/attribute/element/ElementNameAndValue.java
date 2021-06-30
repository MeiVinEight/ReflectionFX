package org.mve.asm.file.attribute.element;

public class ElementNameAndValue
{
	public int name;
	public ElementValue value;

	public int length()
	{
		return 2 + this.value.length();
	}

	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		b[0] = (byte) ((this.name >>> 8) & 0XFF);
		b[1] = (byte) (this.name & 0XFF);
		System.arraycopy(this.value.toByteArray(), 0, b, 2, len-2);
		return b;
	}
}
