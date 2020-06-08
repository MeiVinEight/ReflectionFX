package org.mve;

import org.mve.util.invoke.ReflectionAccessor;
import org.mve.util.invoke.ReflectionFactory;

import java.lang.invoke.MethodType;

public class Main1
{
	public static void main(String[] args) throws Throwable
	{
		ReflectionAccessor<Void> invoke1 =
			ReflectionFactory.getReflectionAccessor(
				Test.class,
				"staticPrint",
				true,
				false,
				false,
				void.class,
				String.class
			);
		invoke1.invoke("Text");

		ReflectionAccessor<Void> invoke2 =
			ReflectionFactory.getReflectionAccessor(
				Test.class,
				"print",
				false,
				false,
				false,
				void.class,
				String.class
			);
		invoke2.invoke(Test.INSTANCE, "Text");

		ReflectionAccessor<Void> invoke3 =
			ReflectionFactory.getReflectionAccessor(
				Test.class,
				"staticPrint",
				true,
				false,
				false,
				MethodType.methodType(void.class, String.class)
			);
		invoke3.invoke("Text");

		ReflectionAccessor<Void> invoke4 =
			ReflectionFactory.getReflectionAccessor(
				Test.class,
				"print",
				false,
				false,
				false,
				MethodType.methodType(void.class, String.class)
			);
		invoke4.invoke(Test.INSTANCE, "Text");

		ReflectionAccessor<Void> invoke5 =
			ReflectionFactory.getReflectionAccessor(Test.class.getDeclaredMethod("staticPrint", String.class));
		invoke5.invoke("Text");

		ReflectionAccessor<Void> invoke6 =
			ReflectionFactory.getReflectionAccessor(Test.class.getDeclaredMethod("print", String.class));
		invoke6.invoke(Test.INSTANCE, "Text");
	}
}
