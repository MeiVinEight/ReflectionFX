package org.mve.util;

public class SystemUtil
{
	public static void printStackTrace()
	{
		StackTraceElement[] traces = new Throwable().getStackTrace();
		for (int i=1; i<traces.length; i++) System.out.println(traces[i]);
	}
}
