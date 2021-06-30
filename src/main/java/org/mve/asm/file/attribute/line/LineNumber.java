package org.mve.asm.file.attribute.line;

public class LineNumber
{
	public int start;
	public int line;

	public byte[] toByteArray()
	{
		byte[] b = new byte[4];
		b[0] = (byte) ((this.start >>> 8) & 0XFF);
		b[1] = (byte) (this.start & 0XFF);
		b[2] = (byte) ((this.line >>> 8) & 0XFF);
		b[3] = (byte) (this.line & 0XFF);
		return b;
	}
}
