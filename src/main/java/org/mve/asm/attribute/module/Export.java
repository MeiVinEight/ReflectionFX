package org.mve.asm.attribute.module;

import java.util.Arrays;

public class Export
{
	public String name;
	public int flag;
	public String[] to = new String[0];

	public Export name(String name)
	{
		this.name = name;
		return this;
	}

	public Export flag(int flag)
	{
		this.flag = flag;
		return this;
	}

	public Export to(String name)
	{
		this.to = Arrays.copyOf(this.to, this.to.length+1);
		this.to[this.to.length-1] = name;
		return this;
	}
}
