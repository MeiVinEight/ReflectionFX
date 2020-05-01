package org.mve.test;

import org.mve.util.SystemUtil;
import org.mve.util.reflect.ReflectionFactory;

public class Test
{
	private static void method(Object o)
	{
		SystemUtil.printStackTrace();
		System.out.println();
		Class<?>[] cs = ReflectionFactory.ACCESSOR.getClassContext();
		for (Class<?> c : cs) System.out.println(c);
	}

	public static void invoke(Object... obj)
	{
		method(obj);
	}
}
