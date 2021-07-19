package org.mve.asm.attribute.inner;

public class InnerClass
{
	public String inner;
	public String outer;
	public String name;
	public int access;

	public InnerClass(String inner, String outer, String name, int access)
	{
		this.inner = inner;
		this.outer = outer;
		this.name = name;
		this.access = access;
	}
}
