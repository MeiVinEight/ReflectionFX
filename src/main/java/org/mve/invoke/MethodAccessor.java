package org.mve.invoke;

import java.lang.reflect.Method;

public interface MethodAccessor<T> extends ReflectionAccessor<T>
{
	Method getMethod();
}
