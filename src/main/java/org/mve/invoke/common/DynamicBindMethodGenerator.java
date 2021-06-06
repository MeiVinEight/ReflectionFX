package org.mve.invoke.common;

import org.mve.invoke.MethodKind;

public abstract class DynamicBindMethodGenerator extends DynamicBindGenerator
{
	private final MethodKind implementation;
	private final MethodKind invocation;
	private final int kind;

	public DynamicBindMethodGenerator(Class<?> target, MethodKind implementation, MethodKind invocation, int kind)
	{
		super(target);
		this.implementation = implementation;
		this.invocation = invocation;
		this.kind = kind;
	}

	public MethodKind implementation()
	{
		return implementation;
	}

	public MethodKind invocation()
	{
		return invocation;
	}

	public int kind()
	{
		return kind;
	}
}
