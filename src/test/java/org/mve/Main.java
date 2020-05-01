package org.mve;

import org.mve.test.Test;
import org.mve.util.reflect.ReflectionAccessor;
import org.mve.util.reflect.ReflectionFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class Main
{
	public static void main(String[] args) throws Throwable
	{
		MethodHandle handle = ReflectionFactory.TRUSTED_LOOKUP.findStatic(Test.class, "method", MethodType.methodType(void.class, Object.class));
		Method method = ReflectionFactory.ACCESSOR.getMethod(Test.class, "method", Object.class);
		ReflectionFactory.ACCESSOR.setAccessible(method, true);
		ReflectionAccessor<Void> accessor = ReflectionFactory.getReflectionAccessor(Test.class, "method", true, false, false, void.class, Object.class);
		Object obj = new Object();
		long time;
		for (int j = 0; j < 10; j++)
		{
			time = System.nanoTime();
			for (int i = 0; i < 1000000; i++)
			{
				method.invoke(null, obj);
			}
			System.out.print(System.nanoTime() - time);
			System.out.print(' ');

			time = System.nanoTime();
			for (int i = 0; i < 1000000; i++)
			{
				handle.invoke(obj);
			}
			System.out.print(System.nanoTime() - time);
			System.out.print(' ');

			time = System.nanoTime();
			for (int i = 0; i < 1000000; i++)
			{
				accessor.invoke(obj);
			}
			System.out.print(System.nanoTime() - time);
			System.out.print(' ');

			time = System.nanoTime();
			for (int i = 0; i < 1000000; i++)
			{
				Test.invoke(obj);
			}
			System.out.println(System.nanoTime() - time);
		}
	}
}
