package org.mve.util.reflect;

public interface ReflectionAccessor<T>
{
	T invoke(Object... args);

	T invoke();
}
