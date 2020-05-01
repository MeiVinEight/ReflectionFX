package org.mve;

import org.mve.io.RandomAccessByteArray;
import org.mve.util.asm.ConstantPoolFinder;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.file.AttributeCode;
import org.mve.util.asm.file.ClassFile;
import org.mve.util.asm.file.ClassMethod;
import org.mve.util.asm.file.ConstantClass;
import org.mve.util.asm.file.ConstantNameAndType;
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
		instrumentation.addTransformer(new ClassFileTransformer()
		{
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
			{
				ClassFile file = new ClassFile(classfileBuffer);
				ConstantPool pool = file.getConstantPool();
				ConstantClass clazz = (ConstantClass) pool.getConstantPoolElement(file.getThisClassIndex());
				ConstantUTF8 utf = (ConstantUTF8) pool.getConstantPoolElement(clazz.getNameIndex());
				String name = utf.getUTF8();
//				String simpleName = filter(name);
//				if (simpleName != null)
//				{
//					try
//					{
//						FileOutputStream out = new FileOutputStream(simpleName+".class");
//						out.write(classfileBuffer);
//						out.flush();
//						out.close();
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
				return classfileBuffer;
			}
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
		FILTERS.add(name -> name.startsWith("org/mve/util/reflect/ReflectionAccessorImpl"));
		FILTERS.add(name -> name.equals("org/mve/Main"));
	}
}
