package org.mve;

import org.mve.invoke.EnumHelper;
import org.mve.invoke.ReflectionFactory;

import java.util.Arrays;

public class Main5
{
	public static void main(String[] args)
	{
		EnumHelper<Test.TestEnum> helper = ReflectionFactory.enumHelper(Test.TestEnum.class);
		Test.TestEnum value = helper.construct("D", 3);
		System.out.println(Arrays.toString(helper.values()));
		helper.add(value);
		System.out.println(Arrays.toString(helper.values()));
		helper.remove(2);
		System.out.println(Arrays.toString(helper.values()));
		Test.TestEnum a = helper.values()[0];
		helper.values()[0] = null;
		System.out.println(Arrays.toString(Test.TestEnum.values()));
	}
}
