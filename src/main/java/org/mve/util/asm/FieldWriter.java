package org.mve.util.asm;

public class FieldWriter
{
	private String name;
	private Class<?> type;
	private int accessFlag;

	public FieldWriter(String name, Class<?> type, int accessFlag)
	{
		this.name = name;
		this.type = type;
		this.accessFlag = accessFlag;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Class<?> getType()
	{
		return type;
	}

	public void setType(Class<?> type)
	{
		this.type = type;
	}

	public int getAccessFlag()
	{
		return accessFlag;
	}

	public void setAccessFlag(int accessFlag)
	{
		this.accessFlag = accessFlag;
	}
}
