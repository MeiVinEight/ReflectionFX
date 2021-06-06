package org.mve.asm.file;

public class ClassSerializeException extends RuntimeException
{
	public ClassSerializeException(){}

	public ClassSerializeException(String msg)
	{
		super(msg);
	}

	public ClassSerializeException(String msg, Throwable caused)
	{
		super(msg, caused);
	}
}
