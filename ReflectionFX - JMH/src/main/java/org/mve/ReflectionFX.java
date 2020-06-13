package org.mve;

import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;
import org.openjdk.jmh.annotations.Benchmark;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class ReflectionFX
{
	private static final Method METHOD;
	private static final MethodHandle HANDLE;
	private static final ReflectionAccessor<String> ACCESSOR;
	private static final BindSite SITE;

	@Benchmark
	public void testMethod() throws Throwable
	{
		METHOD.invoke(Test.class, 1, 2);
	}

	@Benchmark
	public void testMethodHandle() throws Throwable
	{
		HANDLE.invoke(1, 2);
	}

	@Benchmark
	public void testReflectionAccessor()
	{
		ACCESSOR.invoke(1, 2);
	}

	@Benchmark
	public void testDynamicBinding()
	{
		SITE.a(1, 2);
	}

	static
	{
		try
		{
			METHOD = Test.class.getDeclaredMethod("a", int.class, double.class);
			METHOD.setAccessible(true);
			HANDLE = ReflectionFactory.TRUSTED_LOOKUP.findStatic(Test.class, "a", MethodType.methodType(String.class, int.class, double.class));
			ACCESSOR = ReflectionFactory.getReflectionAccessor(Test.class, "a", true, false, false, String.class, int.class, double.class);
			SITE = new ReflectionFactory(BindSite.class, Test.class)
				.method(
					new MethodKind("a", String.class, int.class, double.class),
					new MethodKind("a", String.class, int.class, double.class),
					ReflectionFactory.KIND_INVOKE_STATIC
				)
				.allocate();
		}
		catch (Throwable t)
		{
			ReflectionFactory.ACCESSOR.throwException(t);
			throw new RuntimeException();
		}
	}
}
