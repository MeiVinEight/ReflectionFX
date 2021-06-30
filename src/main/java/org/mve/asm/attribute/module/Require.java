package org.mve.asm.attribute.module;

public class Require
{
	public String name;
	public int flag;
	public String version;

	public Require name(String name)
	{
		this.name = name;
		return this;
	}

	public Require flag(int flag)
	{
		this.flag = flag;
		return this;
	}

	public Require version(String version)
	{
		this.version = version;
		return this;
	}
}
