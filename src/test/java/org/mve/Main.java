package org.mve;

import org.mve.invoke.ReflectionFactory;

import java.lang.invoke.MethodHandles;

public class Main
{
	public static void main(String[] args)
	{
		MethodHandles.Lookup lookup = ReflectionFactory.TRUSTED_LOOKUP;
	}
}
