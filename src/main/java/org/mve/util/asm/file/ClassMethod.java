package org.mve.util.asm.file;

import java.util.Objects;

public class ClassMethod
{
	private short accessFlag;
	private short nameIndex;
	private short descriptorIndex;
	private short attributeCount;
	private Attribute[] attributes = new Attribute[0];

	public short getAccessFlag()
	{
		return accessFlag;
	}

	public void setAccessFlag(short accessFlag)
	{
		this.accessFlag = accessFlag;
	}

	public short getNameIndex()
	{
		return nameIndex;
	}

	public void setNameIndex(short nameIndex)
	{
		this.nameIndex = nameIndex;
	}

	public short getDescriptorIndex()
	{
		return descriptorIndex;
	}

	public void setDescriptorIndex(short descriptorIndex)
	{
		this.descriptorIndex = descriptorIndex;
	}

	public short getAttributeCount()
	{
		return attributeCount;
	}

	public void setAttributeCount(short attributeCount)
	{
		this.attributeCount = attributeCount;
	}

	public Attribute getAttribute(int index)
	{
		return this.attributes[index];
	}

	public void setAttribute(int index, Attribute attribute)
	{
		this.attributes[index] = Objects.requireNonNull(attribute);
	}

	public void addAttribute(Attribute attribute)
	{
		Attribute[] arr = new Attribute[this.attributeCount+1];
		System.arraycopy(this.attributes, 0, arr, 0, this.attributeCount);
		arr[this.attributeCount] = Objects.requireNonNull(attribute);
		this.attributes = arr;
		this.attributeCount++;
	}
}
