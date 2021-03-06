package org.mve.invoke.common;

import org.mve.invoke.ReflectionAccessor;

public abstract class AllocatorGenerator extends AccessorGenerator
{
	public AllocatorGenerator(Class<?> target)
	{
		super(target);
		this.bytecode().interfaces = new String[]{Generator.getType(ReflectionAccessor.class)};
	}
}
