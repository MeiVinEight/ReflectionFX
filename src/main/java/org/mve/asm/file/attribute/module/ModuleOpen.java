package org.mve.asm.file.attribute.module;

import java.util.Arrays;

public class ModuleOpen
{
	public int open;
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
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = (byte) ((this.open >>> 8) & 0XFF);
		b[index++] = (byte) (this.open & 0XFF);
		b[index++] = (byte) ((this.flag >>> 8) & 0XFF);
		b[index++] = (byte) (this.flag & 0XFF);
		b[index++] = (byte) ((this.to.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.to.length & 0XFF);
		for (int s : this.to)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
