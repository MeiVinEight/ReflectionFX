package org.mve;

import org.mve.invoke.ConstructorAccessor;
import org.mve.invoke.ReflectionFactory;

import java.lang.invoke.MethodType;

public class Main4
{
	public static void main(String[] args) throws Throwable
	{
		ConstructorAccessor<Test> accessor1 = ReflectionFactory.access(Test.class, MethodType.methodType(void.class));
		System.out.println(accessor1.invoke());

		ConstructorAccessor<Test> accessor2 = ReflectionFactory.access(Test.class, MethodType.methodType(void.class, String.class));
		System.out.println(accessor2.invoke("TestPrefix"));

		ConstructorAccessor<Test> accessor3 = ReflectionFactory.access(Test.class.getDeclaredConstructor(String.class));
		System.out.println(accessor3.invoke("TestPrefix"));
	}
}
