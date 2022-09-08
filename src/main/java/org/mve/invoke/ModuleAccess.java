package org.mve.invoke;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.constant.Type;
import org.mve.invoke.common.JavaVM;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Proxy;

public class ModuleAccess extends ClassLoader
{
	public static final MethodHandle ACCESSIBLE;

	public ModuleAccess()
	{
		super(ModuleAccess.class.getClassLoader());
	}

	public Class<?> loading(byte[] code)
	{
		Class<?> clazz = this.defineClass(null, code, 0, code.length);
		try
		{
			Class.forName(clazz.getName(), true, this);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return clazz;
	}

	public static void accessible(Object module, Object packageName, Object open)
	{
		if (ModuleAccess.ACCESSIBLE != null)
		{
			try
			{
				ModuleAccess.ACCESSIBLE.invokeExact(module, packageName, open);
			}
			catch (Throwable t)
			{
				JavaVM.exception(t);
			}
		}
	}

	static
	{
		MethodHandle accessible = null;
		try
		{
			if (JavaVM.VERSION >= Opcodes.version(9))
			{
				ModuleAccess access = new ModuleAccess();
				Class<?> JLA;
				Class<?> shared;
				switch (JavaVM.VERSION)
				{
					case 53:
					case 54:
					{
						JLA = Class.forName("jdk.internal.misc.JavaLangAccess");
						shared = Class.forName("jdk.internal.misc.SharedSecrets");
						break;
					}
					default:
					{
						JLA = Class.forName("jdk.internal.access.JavaLangAccess");
						shared = Class.forName("jdk.internal.access.SharedSecrets");
					}
				}
				Object proxy = Proxy.newProxyInstance(access, new Class[]{JLA}, (_1, _2, _3) -> null);
				String packageName = proxy.getClass().getPackage().getName();
				String uuid = JavaVM.random();
				String className = packageName.replace('.', '/') + "/" + uuid;
				byte[] code = new ClassWriter()
					.set(
						Opcodes.version(8),
						AccessFlag.PUBLIC | AccessFlag.STATIC,
						className,
						"java/lang/Object",
						new String[0]
					)
					.method(new MethodWriter()
						.set(AccessFlag.STATIC, "<clinit>", "()V")
						.attribute(new CodeWriter()
							.constant(ModuleAccess.class.getTypeName())
							.method(
								Opcodes.INVOKESTATIC,
								"java/lang/Class",
								"forName",
								"(Ljava/lang/String;)Ljava/lang/Class;",
								false
							)
							.method(
								Opcodes.INVOKEVIRTUAL,
								"java/lang/Class",
								"getModule",
								"()Ljava/lang/Module;",
								false
							)
							.instruction(Opcodes.ASTORE_0)
							.constant(new Type(className))
							.method(
								Opcodes.INVOKEVIRTUAL,
								"java/lang/Class",
								"getModule",
								"()Ljava/lang/Module;",
								false
							)
							.constant(packageName)
							.instruction(Opcodes.ALOAD_0)
							.method(
								Opcodes.INVOKEVIRTUAL,
								"java/lang/Module",
								"addExports",
								"(Ljava/lang/String;Ljava/lang/Module;)Ljava/lang/Module;",
								false
							)
							.instruction(Opcodes.POP)
							.instruction(Opcodes.RETURN)
							.max(3, 1)
						)
					)
					.method(new MethodWriter()
						.set(
							AccessFlag.PUBLIC | AccessFlag.STATIC,
							"accessible",
							"(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"
						)
						.attribute(new CodeWriter()
							.method(
								Opcodes.INVOKESTATIC,
								shared.getTypeName().replace('.', '/'),
								"getJavaLangAccess",
								MethodType.methodType(JLA).toMethodDescriptorString(),
								false
							)
							.instruction(Opcodes.ALOAD_0)
							.type(Opcodes.CHECKCAST, "java/lang/Module")
							.instruction(Opcodes.ALOAD_1)
							.type(Opcodes.CHECKCAST, "java/lang/String")
							.instruction(Opcodes.ALOAD_2)
							.type(Opcodes.CHECKCAST, "java/lang/Module")
							.method(
								Opcodes.INVOKEINTERFACE,
								JLA.getTypeName().replace('.', '/'),
								"addOpens",
								"(Ljava/lang/Module;Ljava/lang/String;Ljava/lang/Module;)V",
								true
							)
							.instruction(Opcodes.RETURN)
							.max(4, 3)
						)
					)
					.toByteArray();
				Class<?> clazz = access.loading(code);
				accessible = MethodHandles.lookup().findStatic(
					clazz,
					"accessible",
					MethodType.methodType(void.class, Object.class, Object.class, Object.class)
				);
			}
		}
		catch (Throwable t)
		{
			JavaVM.exception(t);
		}
		ACCESSIBLE = accessible;
	}
}
