package org.mve.invoke.common.polymorphism;

import org.mve.invoke.MethodKind;

public abstract class PolymorphismConstructGenerator extends PolymorphismGenerator
{
	private final MethodKind implementation;
	private final MethodKind invocation;

	public PolymorphismConstructGenerator(Class<?> target, MethodKind implementation, MethodKind invocation)
	{
		super(target);
		this.implementation = implementation;
		this.invocation = invocation;
	}

	public MethodKind implementation()
	{
		return implementation;
	}

	public MethodKind invocation()
	{
		return invocation;
	}
}
