package org.mve.invoke.common;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;

public class JavaVM
{
	public static final String[] CONSTANT = new String[8];
	public static final int VERSION;
	public static final String VENDOR;

	@SuppressWarnings("unchecked")
	public static <T extends RuntimeException> void exception(Throwable t)
	{
		throw (T) t;
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

			CONSTANT[0] = "java/lang/MagicAccessorFactory";
			CONSTANT[1] = VERSION < 57 ? "Ljava/lang/invoke/LambdaForm$Hidden;" : "Ljdk/internal/vm/annotation/Hidden;";
			CONSTANT[2] = VERSION == 0x34 ? "Ljava/lang/invoke/ForceInline;" : "Ljdk/internal/vm/annotation/ForceInline;";
			CONSTANT[3] = "Ljava/lang/invoke/LambdaForm$Compiled;";
			CONSTANT[4] = "0";
			CONSTANT[5] = "1";
			CONSTANT[6] = "00";
			CONSTANT[7] = "generate";
		}
		catch (Throwable t)
		{
			exception(t);
			throw new RuntimeException();
		}
	}
}
