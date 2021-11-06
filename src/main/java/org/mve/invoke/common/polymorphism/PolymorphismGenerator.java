package org.mve.invoke.common.polymorphism;

import org.mve.asm.ClassWriter;
import org.mve.invoke.common.Generator;

public abstract class PolymorphismGenerator extends Generator
{
	private final Class<?> target;

	public PolymorphismGenerator(Class<?> target)
	{
		this.target = target;
	}

	public Class<?> getTarget()
	{
		return target;
	}

	public abstract void generate(ClassWriter bytecode);
}
