package org.mve.invoke;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardReflectionClassLoader extends ClassLoader implements ReflectionClassLoader
{
	private static final MethodHandle DEFINE;
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
		Class<?> clazz = (Class<?>) ReflectionFactory.METHOD_HANDLE_INVOKER.invoke(DEFINE, this.loader, null, code, 0, code.length);
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
			URL url = ClassLoader.getSystemClassLoader().getResource("java/lang/Object.class");
			if (url == null) throw new NullPointerException();
			InputStream in = url.openStream();
			if (6 != in.skip(6)) throw new UnknownError();
			int majorVersion = new DataInputStream(in).readUnsignedShort();
			in.close();
			Class<?> c = Class.forName(majorVersion > 0x34 ? "jdk.internal.reflect.DelegatingClassLoader" : "sun.reflect.DelegatingClassLoader");
			DELEGATING_CLASS_CONSTRUCTOR = ReflectionFactory.TRUSTED_LOOKUP.findConstructor(c, MethodType.methodType(void.class, ClassLoader.class));
			DEFINE = ReflectionFactory.TRUSTED_LOOKUP.findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class));
		}
		catch (Exception e)
		{
			throw new UninitializedException(e);
		}
	}
}
