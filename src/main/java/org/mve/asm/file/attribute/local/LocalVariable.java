package org.mve.asm.file.attribute.local;

public class LocalVariable
{
	public int start;
	public int length;
	public int name;
	public int type;
	public int slot;

	public byte[] toByteArray()
	{
		byte[] b = new byte[10];
		b[0] = (byte) ((this.start >>> 8) & 0XFF);
		b[1] = (byte) (this.start & 0XFF);
		b[2] = (byte) ((this.length >>> 8) & 0XFF);
		b[3] = (byte) (this.length & 0XFF);
		b[4] = (byte) ((this.name >>> 8) & 0XFF);
		b[5] = (byte) (this.name & 0XFF);
		b[6] = (byte) ((this.type >>> 8) & 0XFF);
		b[7] = (byte) (this.type & 0XFF);
		b[8] = (byte) ((this.slot >>> 8) & 0XFF);
		b[9] = (byte) (this.slot & 0XFF);
		return b;
	}
}
