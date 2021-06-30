package org.mve.asm.file.attribute.module;

import java.util.Arrays;

public class ModuleProvide
{
	public int provide;
	public int[] whit = new int[0];

	public void with(int cp)
	{
		this.whit = Arrays.copyOf(this.whit, this.whit.length+1);
		this.whit[this.whit.length-1] = cp;
	}

	public int length()
	{
		return 4 + (2 * this.whit.length);
	}

	public byte[] toByteArray()
	{
		int len = this.length();
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = (byte) ((this.provide >>> 8) & 0XFF);
		b[index++] = (byte) (this.provide & 0XFF);
		b[index++] = (byte) ((this.whit.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.whit.length & 0XFF);
		for (int s : this.whit)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
