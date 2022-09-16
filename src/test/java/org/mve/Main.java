package org.mve;

import org.mve.invoke.MagicAccessor;

public class Main
{
	public static void main(String[] args) throws Throwable
	{
//		MagicAccessor accessor = MagicAccessor.accessor;
//		Unsafe unsafe = Unsafe.unsafe;
//		long method = unsafe.objectFieldOffset(accessor.getField(Method.class, "name"));
//		long field = unsafe.objectFieldOffset(accessor.getField(Field.class, "name"));
//		System.out.println(unsafe.getObject(Main.class.getMethod("main", String[].class), method));
//		System.out.println(unsafe.getObject(String.class.getDeclaredField("value"), field));
		System.out.println(MagicAccessor.accessor.getName(Main.class.getMethod("main", String[].class)));
		System.out.println(MagicAccessor.accessor.getName(String.class.getDeclaredField("value")));
		System.out.println(MagicAccessor.accessor.getPID());
	}
}
