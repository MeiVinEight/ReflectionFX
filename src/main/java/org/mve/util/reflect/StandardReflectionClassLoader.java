package org.mve.util.reflect;

public class StandardReflectionClassLoader extends ClassLoader implements ReflectionClassLoader
{
	public StandardReflectionClassLoader(ClassLoader parent) throws Throwable
	{
	}

	@Override
	public final synchronized Class<?> define(byte[] code)
	{
		return null;
	}
}
