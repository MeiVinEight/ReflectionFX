package org.mve.invoke;

import java.lang.reflect.Method;

public interface MethodAccessor<T> extends ReflectionAccessor<T>
{
	public static final String METHOD = "method";

	Method method();

	MethodAccessor<T> with(Object... argument);
}
