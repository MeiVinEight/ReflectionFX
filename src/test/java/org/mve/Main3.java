package org.mve;

import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;

public class Main3
{
	public static void main(String[] args)
	{
		ReflectionAccessor<Test> accessor1 = ReflectionFactory.access(Test.class);
		System.out.println(accessor1.invoke());
	}
}
