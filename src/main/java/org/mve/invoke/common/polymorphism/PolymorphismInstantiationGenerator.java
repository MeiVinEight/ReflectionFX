package org.mve.invoke.common.polymorphism;

import org.mve.invoke.MethodKind;

public abstract class PolymorphismInstantiationGenerator extends PolymorphismGenerator
{
	private final MethodKind implementation;

	public PolymorphismInstantiationGenerator(Class<?> target, MethodKind implementation)
	{
		super(target);
		this.implementation = implementation;
	}

	public MethodKind implementation()
	{
		return implementation;
	}
}
