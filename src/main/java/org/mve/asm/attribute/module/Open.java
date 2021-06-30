package org.mve.asm.attribute.module;

import java.util.Arrays;

public class Open
{
	public String name;
	public int flag;
	public String[] to = new String[0];

	public Open name(String name)
	{
		this.name = name;
		return this;
	}

	public Open flag(int flag)
	{
		this.flag = flag;
		return this;
	}

	public Open to(String name)
	{
		this.to = Arrays.copyOf(this.to, this.to.length+1);
		this.to[this.to.length-1] = name;
		return this;
	}
}
