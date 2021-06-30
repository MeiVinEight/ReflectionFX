package org.mve.asm.file.constant;

import java.util.Arrays;
import java.util.Objects;

public class ConstantArray
{
	public Constant[] element = new Constant[1];

	public ConstantArray()
	{
		element[0] = new ConstantNull();
	}

	public void add(Constant element)
	{
		this.element = Arrays.copyOf(this.element, this.element.length+1);
		this.element[this.element.length-1] = element;
	}

	public void remove(int i)
	{
		element[i] = null;
		this.element = Arrays.stream(this.element).filter(Objects::nonNull).toArray(Constant[]::new);
	}
}
