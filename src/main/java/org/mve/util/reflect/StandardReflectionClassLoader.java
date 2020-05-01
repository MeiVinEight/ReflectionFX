package org.mve.util.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardReflectionClassLoader extends ClassLoader implements ReflectionClassLoader
{
	private static final MethodHandle DELEGATING_CLASS_CONSTRUCTOR;
	private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
	private final ClassLoader loader;

	public StandardReflectionClassLoader(ClassLoader parent)
	{
		super(parent);
		loader = (ClassLoader) ReflectionFactory.METHOD_HANDLE_INVOKER.invoke(DELEGATING_CLASS_CONSTRUCTOR, this);
	}

	@Override
	public final synchronized Class<?> define(byte[] code)
	{
		Class<?> clazz = ReflectionFactory.UNSAFE.defineClass(null, code, 0, code.length, loader, null);
		this.classes.putIfAbsent(clazz.getTypeName(), clazz);
		return clazz;
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException
	{
		Class<?> c = this.classes.get(name);
		if (c != null) return c;
		c = this.findLoadedClass(name);
		if (c != null) return c;
		return this.getParent().loadClass(name);
	}

	static
	{
		try
		{
			Class<?> c = Class.forName(ReflectionFactory.UNSAFE.getJavaVMVersion() > 0x34 ? "jdk.internal.reflect.DelegatingClassLoader" : "sun.reflect.DelegatingClassLoader");
			DELEGATING_CLASS_CONSTRUCTOR = ReflectionFactory.TRUSTED_LOOKUP.findConstructor(c, MethodType.methodType(void.class, ClassLoader.class));
		}
		catch (Exception e)
		{
			throw new UninitializedException(e);
		}
	}
}
