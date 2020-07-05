package org.mve.invoke;

import java.lang.reflect.Constructor;

public interface ConstructorAccessor<T> extends ReflectionAccessor<T>
{
	Constructor<T> getConstructor();
}
