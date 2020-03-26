package org.mve.util.asm;

import java.util.Objects;

public class AttributeInnerClasses extends Attribute
{
	private short innerClassCount;
	private StructInnerClass[] innerClasses = new StructInnerClass[0];

	public AttributeInnerClasses(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getInnerClassCount()
	{
		return innerClassCount;
	}

	public void addInnerClass(StructInnerClass innerClass)
	{
		Objects.requireNonNull(innerClass);
		StructInnerClass[] arr = new StructInnerClass[++this.innerClassCount];
		System.arraycopy(this.innerClasses, 0, arr, 0, this.innerClasses.length);
		arr[this.innerClasses.length] = innerClass;
		this.innerClasses = arr;
	}

	public void setInnerClass(int index, StructInnerClass innerClass)
	{
		Objects.requireNonNull(innerClass);
		this.innerClasses[index] = innerClass;
	}

	public StructInnerClass getInnerClass(int index)
	{
		return this.innerClasses[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.INNER_CLASSES;
	}

	@Override
	public int getLength()
	{
		return 2 + (8 * this.innerClassCount);
	}
}
