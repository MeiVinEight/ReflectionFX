package org.mve;

import org.mve.util.reflect.ReflectInvokeFactory;

public class Main
{
	public static void main(String[] args)
	{
		System.out.println(ReflectInvokeFactory.getReflectInvoker(
			Main.class,
			"a",
			true,
			int.class,
			int.class,
			int.class
		).invoke(1, 2));
	}

	private static int a(int i1, int i2) { return i1+i2; }
}
