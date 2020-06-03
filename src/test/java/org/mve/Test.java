package org.mve;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class Test
{
	private static void method()
	{
//		new Throwable().printStackTrace();
	}

	public static Object invoke(Object... args)
	{
		method();
		return null;
	}

	public static MethodHandle get() throws Throwable
	{
		return MethodHandles.lookup().findStatic(Test.class, "method", MethodType.methodType(void.class));
	}
}
