package org.mve.invoke.common;

public class UninitializedException extends RuntimeException
{
	public UninitializedException(Throwable cased)
	{
		super("Can not initialized class", cased);
	}
}
