package org.mve.asm.file.attribute;

import java.util.Arrays;

public class AttributeModulePackages extends Attribute
{
	public int[] packages = new int[0];

	public void packages(int pkg)
	{
		this.packages = Arrays.copyOf(this.packages, this.packages.length+1);
		this.packages[this.packages.length-1] = pkg;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.MODULE_PACKAGES;
	}

	@Override
	public int length()
	{
		return 8 + (2 * this.packages.length);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((name >>> 8) & 0XFF);
		b[index++] = (byte) (name & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) ((this.packages.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.packages.length & 0XFF);
		for (int s : this.packages)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
