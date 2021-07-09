package org.mve.asm.constant;

public class MethodHandle
{
	public int kind;
	public String type;
	public String name;
	public String sign;

	public MethodHandle(int kind, String type, String name, String sign)
	{
		this.kind = kind;
		this.type = type;
		this.name = name;
		this.sign = sign;
	}

	public MethodHandle()
	{
	}

	public MethodHandle kind(int kind)
	{
		this.kind = kind;
		return this;
	}

	public MethodHandle type(String clazz)
	{
		this.type = clazz;
		return this;
	}

	public MethodHandle name(String name)
	{
		this.name = name;
		return this;
	}

	public MethodHandle sign(String type)
	{
		this.sign = type;
		return this;
	}
}
