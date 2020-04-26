package org.mve.util.asm;

public class ClassWriter
{
	private int majorVersion;
	private String name;
	private int accessFlag;
	private String superClass;
	private String[] interfaces;
	private String signature;

	public ClassWriter(int majorVersion, String name, int accessFlag, String superClass, String[] interfaces, String signature)
	{
		this.majorVersion = majorVersion;
		this.name = name;
		this.accessFlag = accessFlag;
		this.superClass = superClass;
		this.interfaces = interfaces;
		this.signature = signature;
	}

	public int getMajorVersion()
	{
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion)
	{
		this.majorVersion = majorVersion;
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

	public String getSuperClass()
	{
		return superClass;
	}

	public void setSuperClass(String superClass)
	{
		this.superClass = superClass;
	}

	public String getSignature()
	{
		return signature;
	}

	public void setSignature(String signature)
	{
		this.signature = signature;
	}

	public String[] getInterfaces()
	{
		return interfaces;
	}

	public void setInterfaces(String[] interfaces)
	{
		this.interfaces = interfaces;
	}
}
