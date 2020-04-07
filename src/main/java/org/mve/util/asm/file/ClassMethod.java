package org.mve.util.asm.file;

import org.mve.util.Binary;

import java.util.Objects;

public class ClassMethod implements Binary
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

	@Override
	public byte[] toByteArray()
	{
		int len = 8;
		for (Attribute a : this.attributes) len += a.getLength();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((this.accessFlag >>> 8) & 0XFF);
		b[index++] = (byte) (this.accessFlag & 0XFF);
		b[index++] = (byte) ((this.nameIndex >>> 8) & 0XFF);
		b[index++] = (byte) (this.nameIndex & 0XFF);
		b[index++] = (byte) ((this.descriptorIndex >>> 8) & 0XFF);
		b[index++] = (byte) (this.descriptorIndex & 0XFF);
		b[index++] = (byte) ((this.attributeCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.attributeCount & 0XFF);
		for (Attribute a : this.attributes)
		{
			int l = a.getLength();
			System.arraycopy(a.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
