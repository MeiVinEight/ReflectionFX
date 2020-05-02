package org.mve.test;


import org.mve.util.SystemUtil;
import org.mve.util.reflect.ReflectionFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class Test
{
	private static void method()
	{
		new Throwable().printStackTrace(System.out);
	}

	public static void invoke(Object... obj)
	{
		method();
	}

	public static MethodHandle get() throws Throwable
	{
		return MethodHandles.lookup().findStatic(Test.class, "method", MethodType.methodType(void.class));
	}
}
