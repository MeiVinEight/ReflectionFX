package org.mve.util.reflect;

import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardReflectionClassLoader extends ClassLoader implements ReflectionClassLoader
{
	private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
	private final ClassLoader loader;

	public StandardReflectionClassLoader(ClassLoader parent) throws Throwable
	{
		super(parent.getParent());
		ReflectInvokeFactory.TRUSTED_LOOKUP.findSetter(ClassLoader.class, "parent", ClassLoader.class).invoke(parent, this);
		loader = (ClassLoader) ReflectInvokeFactory.TRUSTED_LOOKUP.findConstructor(ReflectInvokeFactory.DELEGATING_CLASS, MethodType.methodType(void.class, ClassLoader.class)).invoke(parent);
	}

	@Override
	public final synchronized Class<?> define(byte[] code)
	{
		Class<?> clazz = (Class<?>) ReflectInvokeFactory.METHOD_HANDLE_INVOKER.invoke(ReflectInvokeFactory.DEFINE, loader, null, code, 0, code.length);
		this.classes.put(clazz.getTypeName(), clazz);
		return clazz;
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException
	{
		Class<?> c = this.classes.get(name);
		if (c != null) return c;
		c = this.findLoadedClass(name);
		if (c != null) return c;
		c = findSystemClass(name);
		if (c != null) return c;
		return this.getParent().loadClass(name);
	}
}