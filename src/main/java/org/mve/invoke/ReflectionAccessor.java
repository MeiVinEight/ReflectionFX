package org.mve.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ReflectionAccessor<T>
{
	public static final String INVOKE			= "invoke";
	public static final String OBJECTIVE		= "objective";
	public static final String ARGUMENT			= "argument";
	public static final String WITH				= "with";

	/**
	 * Method name of regenerate accessoro with arguments
	 * </br></br>
	 * @see org.mve.invoke.common.Generator#generate(Method, int, Object[])
	 * @see org.mve.invoke.common.Generator#generate(Field, Object[])
	 * @see org.mve.invoke.common.Generator#generate(Constructor, Object[])
	 */
	public static final String METHOD_GENERATE	= "generate";

	/**
	 * offset of string in constant array
	 * </br>
	 * @see org.mve.invoke.common.JavaVM#CONSTANT
	 */
	public static final int FIELD_CLASS			= 4;
	public static final int FIELD_OBJECTIVE		= 5;
	public static final int FIELD_WITH 			= 6;

	T invoke(Object... args);

	T invoke();

	Class<?> objective();

	Object[] argument();

	ReflectionAccessor<T> with(Object... argument);
}
