package org.mve.invoke.common.polymorphism;

import org.mve.invoke.MethodKind;

public abstract class PolymorphismMethodGenerator extends PolymorphismGenerator
{
	private final MethodKind implementation;
	private final MethodKind invocation;
	private final int kind;

	public PolymorphismMethodGenerator(Class<?> target, MethodKind implementation, MethodKind invocation, int kind)
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
