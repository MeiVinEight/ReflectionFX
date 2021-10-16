package org.mve.invoke;

public interface ReflectionAccessor<T>
{
	public static final String INVOKE = "invoke";
	public static final String OBJECTIVE = "objective";
	public static final String ARGUMENT = "argument";
	public static final String WITH = "with";

	T invoke(Object... args);

	T invoke();

	Class<?> objective();

	Object[] argument();

	ReflectionAccessor<T> with(Object... argument);
}
