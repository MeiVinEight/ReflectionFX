package org.mve.asm.constant;

public class MethodType
{
	public String type;

	public MethodType(String type)
	{
		this.type = type;
	}

	public MethodType()
	{
	}

	public MethodType type(String type)
	{
		this.type = type;
		return this;
	}
}
