package org.mve;

import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.util.SystemUtil;

import java.util.Arrays;

public class Main
{
	public static void main(String[] args)
	{
		ReflectionAccessor<Void> a = ReflectionFactory.getReflectionAccessor(Main.class, "a", true, false, false, void.class);
		a.invoke();
		a = ReflectionFactory.getReflectionAccessor(Main.class, "a", true, false, false, void.class);
		a.invoke();
	}

	private static void a()
	{
		SystemUtil.printStackTrace();
		System.out.println(Arrays.toString(ReflectionFactory.ACCESSOR.getClassContext()));
	}
}
