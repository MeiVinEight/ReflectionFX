package org.mve.invoke;

public abstract class AllocatorGenerator extends AccessorGenerator
{
	public AllocatorGenerator(Class<?> target)
	{
		super(target);
		this.bytecode().setInterfaces(new String[]{Generator.getType(ReflectionAccessor.class)});
	}
}
