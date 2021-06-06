package org.mve;

import org.mve.invoke.MethodKind;
import org.mve.invoke.PolymorphismFactory;
import org.mve.invoke.ReflectionFactory;

import java.util.Arrays;

public class Main6
{
	public static void main(String[] args)
	{
		BindSite site =
			new PolymorphismFactory<>(BindSite.class)
				.construct(
					Test.class,
					new MethodKind("newTest", Test.class, String.class),
					new MethodKind(null, void.class, String.class)
				)
				.instantiate(Test.class, new MethodKind("allocateTest", Test.class))
				.method(
					Test.class,
					new MethodKind("staticPrint", void.class, String.class),
					new MethodKind("staticPrint", void.class, String.class),
					ReflectionFactory.KIND_INVOKE_STATIC
				)
				.method(
					Test.class,
					new MethodKind("print", void.class, Test.class, String.class),
					new MethodKind("print", void.class, String.class),
					ReflectionFactory.KIND_INVOKE_VIRTUAL
				)
				.field(Test.class, new MethodKind("setInstance", void.class, Test.class), "INSTANCE", ReflectionFactory.KIND_PUT)
				.field(Test.class, new MethodKind("setStaticPrefix", void.class, String.class), "staticPrefix", ReflectionFactory.KIND_PUT)
				.field(Test.class, new MethodKind("setDefaultPrefix", void.class, Test.class, String.class), "defaultPrefix", ReflectionFactory.KIND_PUT)
				.field(Test.class, new MethodKind("setPrefix", void.class, Test.class, String.class), "prefix", ReflectionFactory.KIND_PUT)
				.field(Test.class, new MethodKind("getStaticPrefix", String.class), "staticPrefix", ReflectionFactory.KIND_GET)
				.field(Test.class, new MethodKind("getDefaultPrefix", String.class, Test.class), "defaultPrefix", ReflectionFactory.KIND_GET)
				.field(Test.class, new MethodKind("getPrefix", String.class, Test.class), "prefix", ReflectionFactory.KIND_GET)
				.allocate();
		System.out.println(site.newTest("A"));
		System.out.println(site.allocateTest());
		site.staticPrint("A");

		System.out.println();

		EnumSite enumSite = new PolymorphismFactory<>(EnumSite.class).enumHelper(Test.TestEnum.class).allocate();
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
