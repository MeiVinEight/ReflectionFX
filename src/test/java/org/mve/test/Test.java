package org.mve.test;

import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionFactory;

import java.util.Arrays;

public class Test
{
	private static void a()
	{
		System.out.println(Arrays.toString(ReflectionFactory.ACCESSOR.getClassContext()));
		BS bs = new ReflectionFactory(BS.class, SecurityManager.class).method(new MethodKind("getClassContext", Class[].class, SecurityManager.class), new MethodKind("getClassContext", Class[].class), ReflectionFactory.KIND_INVOKE_SPECIAL).allocate();
		System.out.println(Arrays.toString(bs.getClassContext(new SecurityManager())));
	}

	private interface BS
	{
		Class<?>[] getClassContext(SecurityManager sm);
	}
}
