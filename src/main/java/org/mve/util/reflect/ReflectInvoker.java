package org.mve.util.reflect;

public interface ReflectInvoker<T>
{
	T invoke(Object... args);
}
