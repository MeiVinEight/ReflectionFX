package org.mve.util.asm;

public class MethodWriter
{
	private String name;
	private int accessFlag;
	private Class<?> returnType;
	private Class<?> parameters;

	public MethodWriter(String name, int accessFlag, Class<?> returnType, Class<?> parameters)
	{
		this.name = name;
		this.accessFlag = accessFlag;
		this.returnType = returnType;
		this.parameters = parameters;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getAccessFlag()
	{
		return accessFlag;
	}

	public void setAccessFlag(int accessFlag)
	{
		this.accessFlag = accessFlag;
	}

	public Class<?> getReturnType()
	{
		return returnType;
	}

	public void setReturnType(Class<?> returnType)
	{
		this.returnType = returnType;
	}

	public Class<?> getParameters()
	{
		return parameters;
	}

	public void setParameters(Class<?> parameters)
	{
		this.parameters = parameters;
	}
}
