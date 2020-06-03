package org.mve;

import org.mve.util.asm.file.ClassFile;
import org.mve.util.asm.file.ClassMethod;
import org.mve.util.asm.file.ConstantClass;
import org.mve.util.asm.file.ConstantPool;
import org.mve.util.asm.file.ConstantUTF8;

import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class Transformer
{
	private static final List<Function<String, Boolean>> FILTERS = new LinkedList<>();

	public static void premain(String agentArgs, Instrumentation instrumentation)
	{
		instrumentation.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) ->
		{
			ClassFile file = new ClassFile(classfileBuffer);
			ConstantPool pool = file.getConstantPool();
			ConstantClass clazz = (ConstantClass) pool.getConstantPoolElement(file.getThisClassIndex());
			ConstantUTF8 utf = (ConstantUTF8) pool.getConstantPoolElement(clazz.getNameIndex());
			String name = utf.getUTF8();
			System.out.println(name);
			for (int i=0; i<file.getMethodCount(); i++)
			{
				ClassMethod method = file.getMethod(i);
				String methodname = ((ConstantUTF8) pool.getConstantPoolElement(method.getNameIndex())).getUTF8();
				if (methodname.equals("invokeStatic_L_V"))
				{
					try
					{
						FileOutputStream out = new FileOutputStream("invokeStatic_L_V.class");
						out.write(classfileBuffer);
						out.flush();
						out.close();
					}
					catch (Exception E)
					{
						E.printStackTrace();
					}
					return classfileBuffer;
				}
			}
			return classfileBuffer;
		});
	}

	private static String filter(String name)
	{
		for (Function<String, Boolean> filter : FILTERS) if (filter.apply(name)) return name.substring(name.lastIndexOf('/')+1);
		return null;
	}

	static
	{
		FILTERS.add(name -> name.startsWith("sun/reflect/GeneratedMethodAccessor"));
	}
}
