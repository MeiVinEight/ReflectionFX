package org.mve.asm.file.attribute;

import java.util.Arrays;

public class AttributePermittedSubclasses extends Attribute
{
	public int[] classes = new int[0];

	public void classes(int index)
	{
		int i = this.classes.length;
		this.classes = Arrays.copyOf(classes, i+1);
		this.classes[i] = index;
	}

	@Override
	public AttributeType type()
	{
		return AttributeType.PERMITTED_SUBCLASSES;
	}

	@Override
	public int length()
	{
		return 8 + (2 * this.classes.length);
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[0];
	}
}
