package org.mve;

public class Test
{
	public static final Test INSTANCE = new Test();
	private static String staticPrefix = "StaticPrefix";
	private final String defaultPrefix;
	private String prefix;

	public Test(String prefix)
	{
		this.defaultPrefix = "Default"+prefix;
		this.prefix = prefix;
	}

	public Test() { this("Prefix"); }

	private static void staticPrint(String text) { System.out.println(staticPrefix + " - " + text); }

	private void print(String text) { System.out.println(prefix + " - " + text); }

	public String getPrefix() { return this.prefix; }

	public String getDefaultPrefix() { return this.defaultPrefix; }

	public static String getStaticPrefix() { return staticPrefix; }

	public static enum TestEnum
	{
		A, B, C;
	}
}
