package org.mve.invoke;

import java.lang.reflect.Field;

public interface FieldAccessor<T> extends ReflectionAccessor<T>
{
	public static final String FIELD = "field";
	public static final String GET = "get";
	public static final String SET = "set";

	Field field();

	T get();

	T get(Object o);

	void set(Object vo);

	void set(Object o, Object v);

	FieldAccessor<T> with(Object... argument);
}
