package org.mve.invoke;

import java.lang.reflect.Field;

public interface FieldAccessor<T> extends ReflectionAccessor<T>
{
	Field getField();

	T get();

	T get(Object o);

	void set(Object vo);

	void set(Object o, Object v);
}
