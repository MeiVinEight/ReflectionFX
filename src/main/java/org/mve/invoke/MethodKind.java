package org.mve.invoke;

import org.mve.invoke.common.JavaVM;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class MethodKind
{
	private final Class<?> clazz;
	private final String name;
	private final MethodType type;

	public MethodKind(String name, Class<?> returnType, Class<?>... params)
	{
		this(name, MethodType.methodType(returnType, params));
	}

	public MethodKind(String name, MethodType type)
	{
		this(null, name, type);
	}

	public MethodKind(Method method)
	{
		this(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes());
	}

	public MethodKind(Class<?> clazz, String name, Class<?> returnType, Class<?>... params)
	{
		this(clazz, name, MethodType.methodType(returnType, params));
	}

	public MethodKind(Class<?> clazz, String name, MethodType type)
	{
		this.clazz = clazz;
		this.name = name;
		this.type = type;
	}

	public Class<?> clazz() { return this.clazz; }
	public String name() { return this.name; }
	public MethodType type() { return this.type; }

	public static MethodKind getMethod(MethodKind[] pattern)
	{
		try
		{
			return new MethodKind(JavaVM.getMethod(pattern));
		}
		catch (Throwable ignored)
		{
		}
		return new MethodKind(null, void.class);
	}
}
