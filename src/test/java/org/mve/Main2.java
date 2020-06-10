package org.mve;

import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;

public class Main2
{
	public static void main(String[] args)
	{
		ReflectionAccessor<Test> accessor1 =
			ReflectionFactory.getReflectionAccessor(
				Test.class,
				"INSTANCE",
				Test.class,
				true,
				true
			);
	}
}
