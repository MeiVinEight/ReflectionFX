package org.mve.asm.file.attribute.bootstrap;

import java.util.Arrays;

public class BootstrapMethod
{
	public int reference;
	public int[] argument = new int[0];

	public void argument(int cp)
	{
		this.argument = Arrays.copyOf(this.argument, this.argument.length + 1);
		this.argument[this.argument.length-1] = cp;
	}

	public int length()
	{
		return 4 + (2 * this.argument.length);
	}

	public byte[] toByteArray()
	{
		byte[] b = new byte[this.length()];
		int index = 0;
		b[index++] = (byte) ((this.reference >>> 8) & 0XFF);
		b[index++] = (byte) (this.reference & 0XFF);
		b[index++] = (byte) ((this.argument.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.argument.length & 0XFF);
		for (int s : this.argument)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
