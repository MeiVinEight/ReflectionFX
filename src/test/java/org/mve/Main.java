package org.mve;

import org.mve.util.reflect.Accessor;
import org.mve.util.reflect.ReflectInvokeFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class Main
{
	public static void main(String[] args) throws Throwable
	{
		Accessor acc = ReflectInvokeFactory.ACCESSOR;
		System.err.println("A");
		Field f = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
//		ReflectInvokeFactory.ACCESSOR.setAccessible(f, true);
//		f.setAccessible(true);
		acc.setAccessible(f, true);
		System.out.println(f.get(null));
	}
}
