package org.mve;

import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionFactory;

import java.util.Arrays;

public class Main6
{
	public static void main(String[] args)
	{
		BindSite site =
			new ReflectionFactory(BindSite.class, Test.class)
				.construct(
					new MethodKind("newTest", Test.class, String.class),
					new MethodKind(null, void.class, String.class)
				)
				.instantiation(new MethodKind("allocateTest", Test.class))
				.method(
					new MethodKind("staticPrint", void.class, String.class),
					new MethodKind("staticPrint", void.class, String.class),
					ReflectionFactory.KIND_INVOKE_STATIC
				)
				.method(
					new MethodKind("print", void.class, Test.class, String.class),
					new MethodKind("print", void.class, String.class),
					ReflectionFactory.KIND_INVOKE_VIRTUAL
				)
				.field(new MethodKind("setInstance", void.class, Test.class), "INSTANCE", ReflectionFactory.KIND_PUT)
				.field(new MethodKind("setStaticPrefix", void.class, String.class), "staticPrefix", ReflectionFactory.KIND_PUT)
				.field(new MethodKind("setDefaultPrefix", void.class, Test.class, String.class), "defaultPrefix", ReflectionFactory.KIND_PUT)
				.field(new MethodKind("setPrefix", void.class, Test.class, String.class), "prefix", ReflectionFactory.KIND_PUT)
				.field(new MethodKind("getStaticPrefix", String.class), "staticPrefix", ReflectionFactory.KIND_GET)
				.field(new MethodKind("getDefaultPrefix", String.class, Test.class), "defaultPrefix", ReflectionFactory.KIND_GET)
				.field(new MethodKind("getPrefix", String.class, Test.class), "prefix", ReflectionFactory.KIND_GET)
				.allocate();
		System.out.println(site.newTest("A"));
		System.out.println(site.allocateTest());
		site.staticPrint("A");

		System.out.println();

		EnumSite enumSite = new ReflectionFactory(EnumSite.class, Test.TestEnum.class).enumHelper().allocate();
		System.out.println(Arrays.toString(enumSite.values()));
		enumSite.values(new Test.TestEnum[3]);
		System.out.println(Arrays.toString(Test.TestEnum.values()));
	}

	public interface BindSite
	{
		Test newTest(String prefix);

		Test allocateTest();

		void staticPrint(String text);

		void print(Test obj, String text);

		void setInstance(Test value);

		void setStaticPrefix(String prefix);

		void setDefaultPrefix(Test obj, String prefix);

		void setPrefix(Test obj, String prefix);

		String getStaticPrefix();

		String getDefaultPrefix(Test obj);

		String getPrefix(Test obj);
	}

	public interface EnumSite
	{
		Test.TestEnum construct(String name, int ordinal);

		Test.TestEnum[] values();

		void values(Test.TestEnum[] value);
	}
}
