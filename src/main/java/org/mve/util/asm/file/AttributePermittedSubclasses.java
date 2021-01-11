package org.mve.util.asm.file;

import java.util.Arrays;

public class AttributePermittedSubclasses extends Attribute
{
	private short[] classes = new short[0];

	public AttributePermittedSubclasses(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public void addPermittedSubclass(short index)
	{
		int i = this.classes.length;
		this.classes = Arrays.copyOf(classes, i+1);
		this.classes[i] = index;
	}

	public short getPermittedClass(int index)
	{
		return this.classes[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.PERMITTED_SUBCLASSES;
	}

	@Override
	public int getLength()
	{
		return 8 + (2 * this.classes.length);
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[0];
	}
}
