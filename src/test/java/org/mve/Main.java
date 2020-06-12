package org.mve;

import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionFactory;

public class Main
{
	Main(int a) {}

	public static void main(String[] args)
	{
		I i =
			new ReflectionFactory(I.class, Object.class)
			.method(new MethodKind("init", void.class, Object.class), new MethodKind("<init>", void.class), ReflectionFactory.KIND_INVOKE_VIRTUAL)
			.allocate();
		Main m = (Main) ReflectionFactory.UNSAFE.allocateInstance(Main.class);
		i.init(m);
	}

	public interface I
	{
		void init(Object obj);
	}
}
