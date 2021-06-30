package org.mve.asm.file.attribute.code.exception;

public class Exception
{
	public int start;
	public int end;
	public int caught;
	public int type;

	public byte[] toByteArray()
	{
		byte[] b = new byte[8];
		b[0] = (byte) ((this.start >>> 8) & 0XFF);
		b[1] = (byte) (this.start & 0XFF);
		b[2] = (byte) ((this.end >>> 8) & 0XFF);
		b[3] = (byte) (this.end & 0XFF);
		b[4] = (byte) ((this.caught >>> 8) & 0XFF);
		b[5] = (byte) (this.caught & 0XFF);
		b[6] = (byte) ((this.type >>> 8) & 0XFF);
		b[7] = (byte) (this.type & 0XFF);
		return b;
	}
}
