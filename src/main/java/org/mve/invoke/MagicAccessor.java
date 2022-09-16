package org.mve.invoke;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public abstract class MagicAccessor
{
	public abstract int version();
	public abstract void setAccessible(AccessibleObject acc, boolean flag);
	public abstract Class<?> forName(String name);
	public abstract Class<?> forName(String name, boolean initialize, ClassLoader loader);
	public abstract Class<?> defineClass(ClassLoader loader, byte[] code);
	public abstract StackFrame[] frame();
	public abstract Class<?> getCallerClass();
	public abstract Class<?>[] getClassContext();
	public abstract <T> T construct(Class<?> target);
	public abstract <T> T construct(Class<?> target, Class<?>[] paramTypes, Object[] params);
	public abstract Field getField(Class<?> target, String name);
	public abstract Method getMethod(Class<?> clazz, String name, Class<?> returnType, Class<?>... parameterTypes);
	public abstract <T> Constructor<T> getConstructor(Class<?> target, Class<?>... parameterTypes);
	public abstract Field[] getFields(Class<?> clazz);
	public abstract Method[] getMethods(Class<?> clazz);
	public abstract <T> Constructor<T>[] getConstructors(Class<?> target);
	public abstract void throwException(Throwable t);
	public abstract void initialize(Object obj);
	public abstract String getName(Member member);
	public abstract int getPID();
}
