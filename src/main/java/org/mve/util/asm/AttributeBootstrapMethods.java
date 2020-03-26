package org.mve.util.asm;

import java.util.Objects;

public class AttributeBootstrapMethods extends Attribute
{
	private short bootstrapMethodCount;
	private StructBootstrapMethod[] bootstrapMethods = new StructBootstrapMethod[0];

	public AttributeBootstrapMethods(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getBootstrapMethodCount()
	{
		return bootstrapMethodCount;
	}

	public void addBootstrapMethod(StructBootstrapMethod method)
	{
		StructBootstrapMethod[] arr = new StructBootstrapMethod[this.bootstrapMethodCount+1];
		System.arraycopy(this.bootstrapMethods, 0, arr, 0, this.bootstrapMethodCount);
		arr[this.bootstrapMethodCount] = Objects.requireNonNull(method);
		this.bootstrapMethods = arr;
		this.bootstrapMethodCount++;
	}

	public void setBootstrapMethod(int index, StructBootstrapMethod method)
	{
		this.bootstrapMethods[index] = Objects.requireNonNull(method);
	}

	public StructBootstrapMethod getBootstrapMethod(int index)
	{
		return this.bootstrapMethods[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.BOOTSTRAP_METHODS;
	}

	@Override
	public int getLength()
	{
		int len = 2;
		for (StructBootstrapMethod s : this.bootstrapMethods) len += s.getLength();
		return len;
	}
}
