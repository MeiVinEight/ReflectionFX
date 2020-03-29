package org.mve.util.reflect;

import org.jetbrains.org.objectweb.asm.ClassWriter;
import org.jetbrains.org.objectweb.asm.MethodVisitor;
import org.jetbrains.org.objectweb.asm.Opcodes;
import org.mve.util.IO;
import org.mve.util.asm.file.AccessFlag;
import org.mve.util.asm.file.ClassFile;
import org.mve.util.asm.file.ClassMethod;
import org.mve.util.asm.file.ConstantPool;
import org.mve.util.asm.file.ConstantUTF8;
import sun.misc.Unsafe;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Objects;

public class ReflectInvokeFactory
{
	private static final MethodHandle DEFINE;
	private static final String SUPER_CLASS;
	private static int id = 0;

	/**
	 * Dynamic define a class and return a {@link org.mve.util.reflect.ReflectInvoker}
	 * which can invoke the specified method
	 * @param clazz the class which declared specified method
	 * @param methodName name of method which will be called
	 * @param returnType return type of the method
	 * @param params parameters list of the method
	 * @return {@link ReflectInvokeFactory} which can call method without access check
	 * @throws NoSuchMethodException if can not found the method in specified class
	 * @throws ReflectionGenericException if an exception in class define
	 */
	public static ReflectInvoker getReflectInvoker(Class<?> clazz, String methodName, Class<?> returnType, Class<?>... params) throws NoSuchMethodException, ReflectionGenericException
	{
		// find method and throw NoSuchMethodException
		Method method = clazz.getDeclaredMethod(methodName, params);
		// assignment the if the method is static or not
		final boolean isStatic = (method.getModifiers() & Modifier.STATIC) != 0;

		return generic(clazz, methodName, isStatic, returnType, params);
	}

	public static ReflectInvoker getReflectInvoker(ClassLoader classLoader, String className, String methodName, Class<?> returnType, Class<?>... params) throws NoSuchMethodException, ReflectionGenericException, ClassNotFoundException
	{
		Class<?> checkClass = Class.forName(className, false, classLoader);
		URL url = classLoader.getResource(className.replace('.', '/').concat(".class"));
		if (url == null) throw new ClassNotFoundException(className);
		ClassMethod method;
		TRY:
		{
			try
			{
				InputStream in = url.openStream();
				byte[] code = IO.toByteArray(in);
				in.close();
				ClassFile file = new ClassFile(code);
				ConstantPool pool = file.getConstantPool();
				for (int i = 0; i < file.getMethodCount(); i++)
				{
					method = file.getMethod(i);
					if (Objects.requireNonNull(methodName).equals(((ConstantUTF8)pool.getConstantPoolElement(method.getNameIndex())).getUTF8())) break TRY;
				}
				throw new NoSuchMethodException(methodName);
			}
			catch (IOException e)
			{
				throw new ReflectionGenericException("Can not check method name", e);
			}
		}

		return generic(checkClass, methodName, (method.getAccessFlag() & AccessFlag.ACC_STATIC) != 0, returnType, params);
	}

	private static ReflectInvoker generic(Class<?> clazz, String methodName, boolean isStatic, Class<?> returnType, Class<?>... params) throws ReflectionGenericException
	{
		// class name
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		// description of the method
		StringBuilder desc = new StringBuilder("(");
		for (Class<?> param : params) desc.append(getDescription(param));
		desc.append(')').append(getDescription(returnType));
		// binary name of class
		final String owner = clazz.getTypeName().replace('.', '/');
		// locals and stack
		final int localVariableTableSize = 3;
		final int stackSize = params.length + (params.length == 0 ? 0 : (isStatic ? 2 : 3));

		// generic class
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, className, null, SUPER_CLASS, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		// default constructor of the class
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, SUPER_CLASS, "<init>", "()V", false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		// implement method "invoke" in interface "ReflectInvoker"
		mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitCode();
		if (!isStatic) mv.visitVarInsn(Opcodes.ALOAD, 1);
		int i=0;
		for (Class<?> c : params)
		{
			mv.visitVarInsn(Opcodes.ALOAD, 2);
//			mv.visitVarInsn(Opcodes.BIPUSH, i+10);
			mv.visitIntInsn(Opcodes.BIPUSH, i++);
			mv.visitInsn(Opcodes.AALOAD);
			if (c.isPrimitive())
			{
				if (c == byte.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
				else if (c == short.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
				else if (c == int.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
				else if (c == long.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
				else if (c == float.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
				else if (c == double.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
				else if (c == boolean.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "booleanValue", "()Z", false);
				else if (c == char.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
			}
		}
		if (isStatic) mv.visitMethodInsn(Opcodes.INVOKESTATIC, owner, methodName, desc.toString(), false);
		else mv.visitMethodInsn(methodName.equals("<init>") ? Opcodes.INVOKESPECIAL : Opcodes.INVOKEVIRTUAL, owner, methodName, desc.toString(), false);
		if (returnType == void.class) mv.visitInsn(Opcodes.RETURN);
		else
		{
			if (returnType == byte.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
			else if (returnType == short.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
			else if (returnType == int.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			else if (returnType == long.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
			else if (returnType == float.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
			else if (returnType == double.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
			else if (returnType == boolean.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(Z)Ljava/lang/Boolean", false);
			else if (returnType == char.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character", false);
			mv.visitInsn(Opcodes.ARETURN);
		}
//		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(stackSize, localVariableTableSize);
		mv.visitEnd();
		cw.visitEnd();
		byte[] code = cw.toByteArray();

		// load the class's byte code
		try
		{
			Class<?> implClass = (Class<?>) DEFINE.invoke(null, code, 0, code.length);
			return (ReflectInvoker) implClass.getDeclaredConstructor().newInstance();
		}
		catch (Throwable throwable)
		{
			throw new ReflectionGenericException("Can not define class", throwable);
		}
	}

	private static String getDescription(Class<?> clazz)
	{
		StringBuilder name = new StringBuilder();
		while (clazz.isArray())
		{
			name.append('[');
			clazz = clazz.getComponentType();
		}
		if (clazz == void.class) name.append('V');
		else if (clazz == byte.class) name.append('B');
		else if (clazz == short.class) name.append('S');
		else if (clazz == int.class) name.append('I');
		else if (clazz == long.class) name.append('J');
		else if (clazz == float.class) name.append('F');
		else if (clazz == double.class) name.append('D');
		else if (clazz == boolean.class) name.append('Z');
		else if (clazz == char.class) name.append('C');
		else name.append('L').append(clazz.getTypeName().replace('.', '/')).append(';');
		return name.toString();
	}

	static
	{
		MethodHandle handle = null;
		String superClass = null;
		try
		{
			URL url = ClassLoader.getSystemClassLoader().getResource("java/lang/Object.class");
			if (url == null) throw new NullPointerException();
			InputStream in = url.openStream();
			if (6 != in.skip(6)) throw new UnknownError();
//			byte[] code = IO.toByteArray(in);
			int majorVersion = new DataInputStream(in).readShort() & 0XFFFF;
			in.close();

			Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			Unsafe usf = (Unsafe) field.get(null);

			if (majorVersion > 0X34)
			{
				Class<?> clazz = Class.forName("jdk.internal.module.IllegalAccessLogger");
				Field loggerField = clazz.getDeclaredField("logger");
				long offset = usf.staticFieldOffset(loggerField);
				usf.putObjectVolatile(clazz, offset, null);
			}

			if (majorVersion <= 0X34) superClass = "sun/reflect/MagicAccessorImpl";
			else superClass = "jdk/internal/reflect/MagicAccessorImpl";

			String loaderClassName = majorVersion <= 0X34 ? "sun.reflect.DelegatingClassLoader" : "jdk.internal.reflect.DelegatingClassLoader";
			Class<?> clazz = Class.forName(loaderClassName);
			field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
			field.setAccessible(true);
			MethodHandles.Lookup lookup = (MethodHandles.Lookup) field.get(null);
			handle = lookup.findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class)).bindTo(lookup.findConstructor(clazz, MethodType.methodType(void.class, ClassLoader.class)).invoke(ClassLoader.getSystemClassLoader()));
//			handle = lookup.findVirtual(Unsafe.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class)).bindTo(usf);
//			url = ReflectInvokeFactory.class.getClassLoader().getResource("org/mve/util/reflect/ReflectInvoker.class");
//			if (url == null) throw new NullPointerException();
//			in = url.openStream();
//			code = IO.toByteArray(in);
//			handle.invoke(null, code, 0, code.length, null, null);
		}
		catch (UnknownError err)
		{
			throw err;
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		DEFINE = handle;
		SUPER_CLASS = superClass;
	}
}
