package org.mve;

import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;

public class Main4
{
	public static void main(String[] args) throws Throwable
	{
		ReflectionAccessor<Test> accessor1 =
			ReflectionFactory.getReflectionAccessor(
				Test.class,
				true
			);
		System.out.println(accessor1.invoke());

		ReflectionAccessor<Test> accessor2 =
			ReflectionFactory.getReflectionAccessor(
				Test.class,
				String.class
			);
		System.out.println(accessor2.invoke("TestPrefix"));

		ReflectionAccessor<Test> accessor3 =
			ReflectionFactory.getReflectionAccessor(
				Test.class.getDeclaredConstructor(String.class)
			);
		System.out.println(accessor3.invoke("TestPrefix"));
	}
}
