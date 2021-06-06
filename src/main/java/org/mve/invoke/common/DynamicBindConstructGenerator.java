package org.mve.invoke.common;

import org.mve.invoke.MethodKind;

public abstract class DynamicBindConstructGenerator extends DynamicBindGenerator
{
	private final MethodKind implementation;
	private final MethodKind invocation;

	public DynamicBindConstructGenerator(Class<?> target, MethodKind implementation, MethodKind invocation)
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
