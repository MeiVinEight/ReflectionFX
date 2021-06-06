package org.mve.asm.file;

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
		return 8 + (8 * this.innerClassCount);
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
		b[index++] = (byte) ((this.innerClassCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.innerClassCount & 0XFF);
		for (StructInnerClass s : this.innerClasses)
		{
			System.arraycopy(s.toByteArray(), 0, b, index, 8);
			index+=8;
		}
		return b;
	}
}
