package org.mve.test;

public class A
{
	public static void test(String s)
	{
		System.out.println(s);
	}

	private void a(String s)
	{
		System.out.println(s);
	}

	@Override
	public String toString()
	{
		return "A";
	}
}
