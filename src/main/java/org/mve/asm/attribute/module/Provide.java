package org.mve.asm.attribute.module;

import java.util.Arrays;

public class Provide
{
	public String name;
	public String[] with = new String[0];

	public Provide name(String name)
	{
		this.name = name;
		return this;
	}

	public Provide with(String name)
	{
		this.with = Arrays.copyOf(this.with, this.with.length+1);
		this.with[this.with.length-1] = name;
		return this;
	}
}
