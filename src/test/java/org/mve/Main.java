package org.mve;

import org.mve.test.C;
import org.mve.util.reflect.EnumHelper;
import org.mve.util.reflect.ReflectionFactory;

import java.util.Arrays;

public class Main
{
	public static void main(String[] args)
	{
		EnumHelper<C> enumHelper = ReflectionFactory.getEnumHelper(C.class);
		System.out.println(enumHelper);
		System.out.println(Arrays.toString(C.values()));
		C d = enumHelper.construct("D", 3);
		enumHelper.add(d);
		System.out.println(Arrays.toString(C.values()));
		enumHelper.remove(2);
		System.out.println(Arrays.toString(C.values()));
	}
}
