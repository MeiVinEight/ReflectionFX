package org.mve;

import org.mve.invoke.ConstructorAccessor;
import org.mve.invoke.FieldAccessor;
import org.mve.invoke.ReflectionFactory;

import java.lang.invoke.MethodType;

public class Main
{
	private static final Object O = null;
	public static void main(String[] args)
	{
		ConstructorAccessor<Main> ctr = ReflectionFactory.access(Main.class, MethodType.methodType(void.class));
		System.out.println(ctr);
		System.out.println(ctr.getConstructor());
	}

	private static void a()
	{
		new Throwable().printStackTrace(System.out);
	}
}
