package org.mve;

import org.mve.invoke.MethodAccessor;
import org.mve.invoke.ReflectionFactory;

import java.lang.invoke.MethodType;

public class Main1
{
	public static void main(String[] args) throws Throwable
	{
		MethodAccessor<Void> invoke1 = ReflectionFactory.access(
			Test.class, "staticPrint",
			MethodType.methodType(void.class, String.class),
			ReflectionFactory.KIND_INVOKE_STATIC
		);
		invoke1.invoke("Text");

		MethodAccessor<Void> invoke2 = ReflectionFactory.access(
			Test.class, "print",
			MethodType.methodType(void.class, String.class),
			ReflectionFactory.KIND_INVOKE_VIRTUAL
		);
		invoke2.invoke(Test.INSTANCE, "Text");

		MethodAccessor<Void> invoke3 = ReflectionFactory.access(Test.class.getDeclaredMethod("staticPrint", String.class), ReflectionFactory.KIND_INVOKE_STATIC);
		invoke3.invoke("Text");

		MethodAccessor<Void> invoke4 = ReflectionFactory.access(Test.class.getDeclaredMethod("print", String.class), ReflectionFactory.KIND_INVOKE_VIRTUAL);
		invoke4.invoke(Test.INSTANCE, "Text");
	}
}
