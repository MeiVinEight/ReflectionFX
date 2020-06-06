package org.mve.util.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface MagicAccessor
{
	void setAccessible(AccessibleObject acc, boolean flag);

	Class<?> forName(String name);

	Class<?> forName(String name, boolean initialize, ClassLoader loader);

	Class<?> defineClass(ClassLoader loader, byte[] code);

	Class<?> getCallerClass();

	Class<?>[] getClassContext();

	<T> T construct(Class<?> target);

	<T> T construct(Class<?> target, Class<?>[] paramTypes, Object[] params);

	Object invokeMethodHandle(MethodHandle handle, Object... args);

	Field getField(Class<?> target, String name);

	Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes);

	<T> Constructor<T> getConstructor(Class<?> target, Class<?>... parameterTypes);

	Field[] getFields(Class<?> clazz);

	Method[] getMethods(Class<?> clazz);

	<T> Constructor<T>[] getConstructors(Class<?> target);

	void throwException(Throwable t);

	void initialize(Object obj);
}
