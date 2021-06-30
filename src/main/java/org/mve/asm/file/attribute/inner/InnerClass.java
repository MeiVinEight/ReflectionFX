package org.mve.asm.file.attribute.inner;

public class InnerClass
{
	public int inner;
	public int outer;
	public int name;
	public int access;

	public byte[] toByteArray()
	{
		byte[] b = new byte[8];
		b[0] = (byte) ((this.inner >>> 8) & 0XFF);
		b[1] = (byte) (this.inner & 0XFF);
		b[2] = (byte) ((this.outer >>> 8) & 0XFF);
		b[3] = (byte) (this.outer & 0XFF);
		b[4] = (byte) ((this.name >>> 8) & 0XFF);
		b[5] = (byte) (this.name & 0XFF);
		b[6] = (byte) ((this.access >>> 8) & 0XFF);
		b[7] = (byte) (this.access & 0XFF);
		return b;
	}
}
