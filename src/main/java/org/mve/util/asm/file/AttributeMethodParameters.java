package org.mve.util.asm.file;

import java.util.Objects;

public class AttributeMethodParameters extends Attribute
{
	private byte parameterCount;
	private StructMethodParameter[] parameters = new StructMethodParameter[0];

	public AttributeMethodParameters(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public byte getParameterCount()
	{
		return parameterCount;
	}

	public void addMethodParameter(StructMethodParameter parameter)
	{
		StructMethodParameter[] arr = new StructMethodParameter[this.parameterCount+1];
		System.arraycopy(this.parameters, 0, arr, 0, this.parameterCount);
		arr[this.parameterCount] = Objects.requireNonNull(parameter);
		this.parameters = arr;
		this.parameterCount++;
	}

	public void setMethodParameter(int index, StructMethodParameter parameter)
	{
		this.parameters[index] = Objects.requireNonNull(parameter);
	}

	public StructMethodParameter getMethodParameter(int index)
	{
		return this.parameters[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.METHOD_PARAMETERS;
	}

	@Override
	public int getLength()
	{
		return 7 + (this.parameterCount * 4);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[index++] = (byte) (this.getAttributeNameIndex() & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = this.parameterCount;
		for (StructMethodParameter s : this.parameters)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 4);
			index+=4;
		}
		return b;
	}
}
