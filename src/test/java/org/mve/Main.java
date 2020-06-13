package org.mve;

import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.test.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class Main
{
	public static void main(String[] args) throws Throwable
	{
		Method m = Test.class.getDeclaredMethod("a", int.class, double.class);
		m.setAccessible(true);
		MethodHandle handle = ReflectionFactory.TRUSTED_LOOKUP.findStatic(Test.class, "a", MethodType.methodType(String.class, int.class, double.class));
		ReflectionAccessor<String> accessor = ReflectionFactory.getReflectionAccessor(Test.class, "a", true, false, false, String.class, int.class, double.class);
		BindSite site = new ReflectionFactory(BindSite.class, Test.class)
			.method(new MethodKind("a", String.class, int.class, double.class), new MethodKind("a", String.class, int.class, double.class), ReflectionFactory.KIND_INVOKE_STATIC)
			.allocate();
		long time;
		for (int i = 0; i < 10; i++)
		{
			time = System.nanoTime();
			for (int j = 0; j < 1000000; j++)
			{
				m.invoke(Test.class, 1, 2);
			}
			System.out.print(System.nanoTime() - time);
			System.out.print(' ');

			time = System.nanoTime();
			for (int j = 0; j < 1000000; j++)
			{
				handle.invoke(1, 2);
			}
			System.out.print(System.nanoTime() - time);
			System.out.print(' ');

			time = System.nanoTime();
			for (int j = 0; j < 1000000; j++)
			{
				accessor.invoke(1, 2);
			}
			System.out.print(System.nanoTime() - time);
			System.out.print(' ');

			time = System.nanoTime();
			for (int j = 0; j < 1000000; j++)
			{
				site.a(1, 2);
			}
			System.out.print(System.nanoTime() - time);
			System.out.print(' ');

			time = System.nanoTime();
			for (int j = 0; j < 1000000; j++)
			{
				Test.invoke(1, 2);
			}
			System.out.println(System.nanoTime() - time);
		}
	}

	public interface BindSite
	{
		String a(int i, double d);
	}
}
