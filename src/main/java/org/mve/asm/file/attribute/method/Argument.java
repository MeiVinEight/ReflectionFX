package org.mve.asm.file.attribute.method;

public class Argument
{
	public int name;
	public int access;

	public byte[] toByteArray()
	{
		byte[] b = new byte[4];
		b[0] = (byte) ((this.name >>> 8) & 0XFF);
		b[1] = (byte) (this.name & 0XFF);
		b[2] = (byte) ((this.access >>> 8) & 0XFF);
		b[3] = (byte) (this.access & 0XFF);
		return b;
	}
}
