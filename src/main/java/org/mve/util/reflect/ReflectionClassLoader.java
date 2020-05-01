package org.mve.util.reflect;

public interface ReflectionClassLoader
{
	Class<?> define(byte[] code);
}
