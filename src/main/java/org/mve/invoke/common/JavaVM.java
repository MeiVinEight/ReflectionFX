package org.mve.invoke.common;

import org.mve.asm.Opcodes;
import org.mve.invoke.MethodKind;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

public class JavaVM
{
	public static final String[] CONSTANT = new String[7];
	public static final int VERSION;
	public static final String VENDOR;

	public static final int CONSTANT_MAGIC		= 0;
	public static final int CONSTANT_HIDDEN		= 1;
	public static final int CONSTANT_INLINE		= 2;
	public static final int CONSTANT_COMPILED	= 3;

	public static String random()
	{
		return 'Z' + UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
	}

	public static String randomAnonymous(Class<?> clazz)
	{
		String anonymousName = JavaVM.random();
		String packageName;
		if (clazz.getPackage() != null && (packageName = clazz.getPackage().getName()).length() != 0)
		{
			anonymousName = packageName.replace('.', '/') + '/' + anonymousName;
		}
		return anonymousName;
	}

	public static void exception(Throwable t)
	{
		JavaVM.thrown(t);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Throwable> void thrown(Throwable t) throws T
	{
		throw (T) t;
	}

	public static Class<?> forName(String[] pattern)
	{
		for (String name : pattern)
		{
			try
			{
				return Class.forName(name);
			}
			catch (ClassNotFoundException ignored)
			{
			}
		}
		JavaVM.thrown(new ClassNotFoundException(Arrays.toString(pattern)));
		throw new UnknownError();
	}

	public static Method getMethod(MethodKind[] pattern)
	{
		for (MethodKind kind : pattern)
		{
			try
			{
				return kind.clazz().getDeclaredMethod(kind.name(), kind.type().parameterArray());
			}
			catch (NoSuchMethodException ignored)
			{
			}
		}
		JavaVM.thrown(new NoSuchMethodException());
		throw new UnknownError();
	}

	static
	{
		try
		{
			RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
			VENDOR = bean.getVmVendor();
			URL url = ClassLoader.getSystemClassLoader().getResource("java/lang/Object.class");
			if (url == null) throw new NullPointerException();
			InputStream in = url.openStream();
			if (6 != in.skip(6)) throw new UnknownError();
			VERSION = new DataInputStream(in).readUnsignedShort();
			in.close();

			CONSTANT[0] = "java/lang/MagicAccessorFactory" /* "java/lang/Object" */;
			CONSTANT[1] = VERSION < Opcodes.version(13) ? "Ljava/lang/invoke/LambdaForm$Hidden;" : "Ljdk/internal/vm/annotation/Hidden;";
			CONSTANT[2] = VERSION == Opcodes.version(8) ? "Ljava/lang/invoke/ForceInline;" : "Ljdk/internal/vm/annotation/ForceInline;";
			CONSTANT[3] = "Ljava/lang/invoke/LambdaForm$Compiled;";
			CONSTANT[4] = JavaVM.random();
			CONSTANT[5] = JavaVM.random();
			CONSTANT[6] = JavaVM.random();
		}
		catch (Throwable t)
		{
			exception(t);
			throw new RuntimeException();
		}
	}
}
