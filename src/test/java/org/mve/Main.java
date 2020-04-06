package org.mve;

import org.mve.util.reflect.ReflectInvokeFactory;

public class Main
{
	public static void main(String[] args)
	{
		A.a();
	}

	public static class A
	{
		public static void a()
		{
			System.out.println(ReflectInvokeFactory.CALLER.invoke());
		}
	}
}
