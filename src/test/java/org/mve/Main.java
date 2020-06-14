package org.mve;

import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionFactory;
import org.mve.util.SystemUtil;

public class Main
{
	public static void main(String[] args)
	{
		BindSite site = new ReflectionFactory(BindSite.class, Main.class)
			.method(
				new MethodKind("a", void.class, String.class),
				new MethodKind("a", void.class, String.class),
				ReflectionFactory.KIND_INVOKE_STATIC
			)
			.allocate();
		site.a("A");
	}

	public interface BindSite
	{
		void a(String s);
	}

	private static void a(String s)
	{
		System.out.println(s);
		SystemUtil.printStackTrace();
	}
}
