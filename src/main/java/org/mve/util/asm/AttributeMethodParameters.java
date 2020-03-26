package org.mve.util.asm;

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
		return 1 + (this.parameterCount * 4);
	}
}
