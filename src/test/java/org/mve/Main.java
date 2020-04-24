package org.mve;

import org.mve.util.reflect.ReflectionFactory;

public class Main
{
	public static void a()
	{
		System.out.println(ReflectionFactory.STACK_ACCESSOR.getCallerClass());
	}

	public static class A
	{
		public static void main(String[] args)
		{
			Main.a();
		}
	}
}
