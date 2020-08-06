package org.mve.invoke;

public abstract class DynamicBindInstantiationGenerator extends DynamicBindGenerator
{
	private final MethodKind implementation;

	public DynamicBindInstantiationGenerator(Class<?> target, MethodKind implementation)
	{
		super(target);
		this.implementation = implementation;
	}

	public MethodKind implementation()
	{
		return implementation;
	}
}
