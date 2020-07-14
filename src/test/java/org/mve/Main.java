package org.mve;

import org.mve.invoke.ReflectionFactory;

import java.lang.invoke.MethodType;

public class Main
{
	public static void main(String[] args)
	{
		ReflectionFactory.access(Main.class, "a", MethodType.methodType(void.class), ReflectionFactory.KIND_INVOKE_STATIC).invoke();
		ReflectionFactory.access(Main.class, "a", MethodType.methodType(void.class), ReflectionFactory.KIND_INVOKE_STATIC).invoke();
	}

	private static void a()
	{
		new Throwable().printStackTrace(System.out);
	}
}
