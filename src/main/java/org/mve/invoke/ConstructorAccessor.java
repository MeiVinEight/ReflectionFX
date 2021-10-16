package org.mve.invoke;

import java.lang.reflect.Constructor;

public interface ConstructorAccessor<T> extends ReflectionAccessor<T>
{
	public static final String CONSTRUCTOR = "constructor";

	Constructor<T> constructor();

	ConstructorAccessor<T> with(Object... argument);
}
