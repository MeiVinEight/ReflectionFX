package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public abstract class ClassArrayWriter implements AttributeWriter
{
	public String[] classes;

	public ClassArrayWriter(String... member)
	{
		this.classes = member;
	}

	public ClassArrayWriter classes(String name)
	{
		this.classes = Arrays.copyOf(this.classes, this.classes.length+1);
		this.classes[this.classes.length-1] = name;
		return this;
	}

	public int[] array(ConstantArray array)
	{
		int[] arr = new int[this.classes.length];
		for (int i = 0; i < this.classes.length; i++)
		{
			arr[i] = ConstantPoolFinder.findClass(array, this.classes[i]);
		}
		return arr;
	}
}
