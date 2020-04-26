package org.mve;

import org.mve.util.reflect.ReflectionAccessor;
import org.mve.util.reflect.ReflectionFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class Main
{
	public static void main(String[] args) throws Throwable
	{
		ReflectionAccessor<Void> accessor = ReflectionFactory.getReflectionAccessor(
			Main.class,
			"method",
			true,
			false,
			false,
			void.class
		);
		ReflectionFactory.getReflectionAccessor(
			sun.reflect.ReflectionFactory.class,
			"noInflation",
			boolean.class,
			true,
			false
		).invoke(true);
		Method method = Main.class.getDeclaredMethod("method");
		method.setAccessible(true);
		MethodHandle handle = MethodHandles.lookup().findStatic(
			Main.class,
			"method",
			MethodType.methodType(void.class)
		);
		long time;
		for (int i = 0; i < 10; i++)
		{
			time = System.nanoTime();
			for (int j = 0; j < 1000000; j++)
			{
				method.invoke(null);
			}
			System.out.print(System.nanoTime() - time);
			System.out.print(' ');

			time = System.nanoTime();
			for (int j = 0; j < 1000000; j++)
			{
				handle.invoke();
			}
			System.out.print(System.nanoTime() - time);
			System.out.print(' ');

			time = System.nanoTime();
			for (int j = 0; j < 1000000; j++)
			{
				accessor.invoke();
			}
			System.out.print(System.nanoTime() - time);
			System.out.print(' ');

			time = System.nanoTime();
			for (int j = 0; j < 1000000; j++)
			{
				method();
			}
			System.out.println(System.nanoTime() - time);
		}
	}

	private static void method()
	{
	}
}
