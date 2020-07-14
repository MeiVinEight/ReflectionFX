package org.mve;

import org.mve.invoke.FieldAccessor;
import org.mve.invoke.ReflectionFactory;

public class Main2
{
	public static void main(String[] args) throws Throwable
	{
		Test test = new Test();

		FieldAccessor<Test> accessor1 = ReflectionFactory.access(Test.class, "INSTANCE");
		accessor1.invoke(test);
		System.out.println(accessor1.invoke());

		FieldAccessor<String> accessor2 = ReflectionFactory.access(Test.class, "staticPrefix");
		accessor2.invoke("Test");
		System.out.println(accessor2.invoke());

		FieldAccessor<String> accessor3 = ReflectionFactory.access(Test.class.getDeclaredField("defaultPrefix"));
		accessor3.invoke(test, "TestDefaultPrefix");
		System.out.println(accessor3.invoke(test));

		FieldAccessor<String> accessor4 = ReflectionFactory.access(Test.class.getDeclaredField("prefix"));
		accessor4.invoke(test, "TestPrefix");
		System.out.println(accessor4.invoke(test));
	}
}
