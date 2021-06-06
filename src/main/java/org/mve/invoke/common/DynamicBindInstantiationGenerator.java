package org.mve.invoke.common;

import org.mve.invoke.MethodKind;

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
