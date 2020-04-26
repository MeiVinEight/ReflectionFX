package org.mve.util.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AccessibleObject;

public interface Accessor
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
}
