package org.mve;

import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;

public class Main2
{
	public static void main(String[] args) throws Throwable
	{
		Test test = new Test();

		ReflectionAccessor<Test> accessor1 =
			ReflectionFactory.getReflectionAccessor(
				Test.class,
				"INSTANCE",
				Test.class,
				true,
				true
			);
		accessor1.invoke(test);
		System.out.println(accessor1.invoke());

		ReflectionAccessor<String> accessor2 =
			ReflectionFactory.getReflectionAccessor(
				Test.class,
				"staticPrefix",
				String.class,
				true,
				false
			);
		accessor2.invoke("Test");
		System.out.println(accessor2.invoke());

		ReflectionAccessor<String> accessor3 =
			ReflectionFactory.getReflectionAccessor(
				Test.class.getDeclaredField("defaultPrefix")
			);
		accessor3.invoke(test, "TestDefaultPrefix");
		System.out.println(accessor3.invoke(test));

		ReflectionAccessor<String> accessor4 =
			ReflectionFactory.getReflectionAccessor(
				Test.class.getDeclaredField("prefix")
			);
		accessor4.invoke(test, "TestPrefix");
		System.out.println(accessor4.invoke(test));
	}
}
