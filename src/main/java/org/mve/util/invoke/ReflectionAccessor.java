package org.mve.util.invoke;

public interface ReflectionAccessor<T>
{
	T invoke(Object... args);

	T invoke();
}
