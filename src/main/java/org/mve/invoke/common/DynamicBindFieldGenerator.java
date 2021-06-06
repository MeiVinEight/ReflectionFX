package org.mve.invoke.common;

import org.mve.invoke.MethodKind;

public abstract class DynamicBindFieldGenerator extends DynamicBindGenerator
{
	private final MethodKind implementation;
	private final String operation;
	private final int kind;

	public DynamicBindFieldGenerator(Class<?> target, MethodKind implementation, String operation, int kind)
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
