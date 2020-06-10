package org.mve.invoke;

public interface ReflectionClassLoader
{
	Class<?> define(byte[] code);
}
