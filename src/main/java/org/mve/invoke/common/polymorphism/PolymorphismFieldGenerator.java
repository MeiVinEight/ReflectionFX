package org.mve.invoke.common.polymorphism;

import org.mve.invoke.MethodKind;

public abstract class PolymorphismFieldGenerator extends PolymorphismGenerator
{
	private final MethodKind implementation;
	private final String operation;
	private final int kind;

	public PolymorphismFieldGenerator(Class<?> target, MethodKind implementation, String operation, int kind)
	{
		super(target);
		this.implementation = implementation;
		this.operation = operation;
		this.kind = kind;
	}

	public MethodKind implementation()
	{
		return implementation;
	}

	public String operation()
	{
		return operation;
	}

	public int kind()
	{
		return kind;
	}
}
