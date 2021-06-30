package org.mve.asm.file.attribute.module;

import java.util.Arrays;

public class ModuleExport
{
	public int export;
	public int flag;
	public int[] to = new int[0];

	public void to(int cp)
	{
		this.to = Arrays.copyOf(this.to, this.to.length+1);
		this.to[this.to.length-1] = cp;
	}

	public int length()
	{
		return 6 + (2 * this.to.length);
	}

	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		b[0] = (byte) ((this.export >>> 8) & 0XFF);
		b[1] = (byte) (this.export & 0XFF);
		b[2] = (byte) ((this.flag >>> 8) & 0XFF);
		b[3] = (byte) (this.flag & 0XFF);
		b[4] = (byte) ((this.to.length >>> 8) & 0XFF);
		b[5] = (byte) (this.to.length & 0XFF);
		int index = 6;
		for (int s : this.to)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
