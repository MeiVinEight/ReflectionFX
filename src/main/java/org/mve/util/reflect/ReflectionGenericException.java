package org.mve.util.reflect;

public class ReflectionGenericException extends RuntimeException
{
	public ReflectionGenericException(String msg, Throwable caused)
	{
		super(msg, caused);
	}
}
