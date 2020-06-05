package org.mve;

import org.mve.test.A;
import org.mve.util.reflect.MethodKind;
import org.mve.util.reflect.ReflectionFactory;

import java.lang.invoke.MethodType;

public class Main
{
	public static void main(String[] args)
	{
		System.out.println(ReflectionFactory.UNSAFE.allocateInstance(Main.class));
	}
}
