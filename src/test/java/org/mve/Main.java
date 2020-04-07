package org.mve;

import org.mve.util.reflect.ReflectInvokeFactory;
import org.mve.util.reflect.Unsafe;

import java.lang.reflect.Field;

public class Main
{
	private static int a;
	public static void main(String[] args) throws Throwable
	{
		Unsafe usf = ReflectInvokeFactory.UNSAFE;
		Field f = Main.class.getDeclaredField("a");
		System.out.println(usf.staticFieldBase(f));
		System.out.println(usf.staticFieldOffset(f));
	}

	public static void a(long l1, long l2)
	{
		System.out.println(l1+l2);
	}
}
