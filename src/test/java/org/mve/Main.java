package org.mve;

import org.mve.util.reflect.ReflectionFactory;

import java.lang.invoke.MethodType;

public class Main
{
	public Main(double d, int a)
	{
		System.out.println(d);
		System.out.println(a);
	}
	public static void main(String[] args)
	{
		FP fp = ReflectionFactory.reflection(
			FP.class,
			"call",
			MethodType.methodType(Main.class),
			Main.class,
			null,
			null
		);
		System.out.println(fp.call());
	}
}
