package org.mve;

import org.mve.util.reflect.ReflectionFactory;

public class Main
{
	public static void main(String[] args)
	{
		System.out.println(ReflectionFactory.ACCESSOR.forName("org.mve.Main"));
	}
}
