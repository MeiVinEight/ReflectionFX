package org.mve.invoke;

public interface ReflectionAccessor<T>
{
	T invoke(Object... args);

	T invoke();

	Class<?> access();
}
