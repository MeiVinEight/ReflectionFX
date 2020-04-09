package org.mve;

import org.mve.util.reflect.ReflectionFactory;

public class Main
{
	public static void main(String[] args)
	{
		ReflectionFactory.ACCESSOR.defineClass(Main.class.getClassLoader(), new byte[0]);
	}
}
