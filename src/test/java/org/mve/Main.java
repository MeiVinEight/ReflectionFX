package org.mve;

import org.mve.invoke.MagicAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Main
{
	public static void main(String[] args) throws Throwable
	{
//		MagicAccessor accessor = ReflectionFactory.ACCESSOR;
//		Unsafe unsafe = ReflectionFactory.UNSAFE;
//		long method = unsafe.objectFieldOffset(accessor.getField(Method.class, "name"));
//		long field = unsafe.objectFieldOffset(accessor.getField(Field.class, "name"));
//		System.out.println(unsafe.getObject(Main.class.getMethod("main", String[].class), method));
//		System.out.println(unsafe.getObject(String.class.getDeclaredField("value"), field));
		System.out.println(ReflectionFactory.ACCESSOR.getName(Main.class.getMethod("main", String[].class)));
		System.out.println(ReflectionFactory.ACCESSOR.getName(String.class.getDeclaredField("value")));
		System.out.println(ReflectionFactory.ACCESSOR.getPID());
	}
}
