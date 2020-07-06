package org.mve;

import org.mve.invoke.MethodAccessor;
import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionFactory;
import org.mve.util.IO;

import java.io.InputStream;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.net.URLClassLoader;

public class Main
{
	private static Object o;

	public Main()
	{
		System.out.println("Construct");
	}

	public static void main(String[] args) throws Throwable
	{
		URLClassLoader loader = new URLClassLoader(new URL[0], Main.class.getClassLoader());
		InputStream in = Main.class.getClassLoader().getResourceAsStream("org/mve/A.class");
		if (in == null) throw new NullPointerException();
		byte[] classcode = IO.toByteArray(in);
		Class<?> c = ReflectionFactory.UNSAFE.defineClass(null, classcode, 0, classcode.length, loader, null);
		MethodAccessor<Void> main = ReflectionFactory.access(c, "main", MethodType.methodType(void.class, String[].class), ReflectionFactory.KIND_INVOKE_STATIC);
		main.invoke((Object) args);
		System.out.println(main.getMethod());
		System.out.println(main);
	}
}
