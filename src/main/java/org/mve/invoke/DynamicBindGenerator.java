package org.mve.invoke;

import org.mve.util.asm.ClassWriter;

public abstract class DynamicBindGenerator extends Generator
{
	private final Class<?> target;

	public DynamicBindGenerator(Class<?> target)
	{
		this.target = target;
	}

	public Class<?> getTarget()
	{
		return target;
	}

	public abstract void generate(ClassWriter bytecode);
}
