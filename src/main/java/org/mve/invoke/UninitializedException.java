package org.mve.invoke;

public class UninitializedException extends RuntimeException
{
	public UninitializedException(Throwable cased)
	{
		super("Can not initialized class", cased);
	}
}
