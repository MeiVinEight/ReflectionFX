package org.mve;

import org.mve.invoke.ReflectionFactory;

public class Main
{
	public static void main(String[] args) throws Throwable
	{
		System.out.println(Class.forName("java.lang.Class$EnumVars").getTypeName());
	}
}
