package org.mve.util.reflect;

import org.jetbrains.org.objectweb.asm.ClassWriter;
import org.jetbrains.org.objectweb.asm.Label;
import org.jetbrains.org.objectweb.asm.MethodVisitor;
import org.jetbrains.org.objectweb.asm.Opcodes;
import org.jetbrains.org.objectweb.asm.Type;
import org.mve.util.IO;
import org.mve.util.asm.file.AccessFlag;
import org.mve.util.asm.file.ClassField;
import org.mve.util.asm.file.ClassFile;
import org.mve.util.asm.file.ClassMethod;
import org.mve.util.asm.file.ConstantClass;
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
import java.net.URL;
import java.util.Objects;

public class ReflectInvokeFactory
{
	private static final MethodHandle DEFINE;
	private static final String SUPER_CLASS;
	private static final Unsafe USF;
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
		return checkMethodAndGeneric(clazz.getClassLoader(), clazz, methodName, returnType, params);
	}

	public static ReflectInvoker getReflectInvoker(ClassLoader classLoader, String className, String methodName, Class<?> returnType, Class<?>... params) throws NoSuchMethodException, ReflectionGenericException, ClassNotFoundException
	{
		Class<?> checkClass = Class.forName(className, false, classLoader);
		return checkMethodAndGeneric(classLoader, checkClass, methodName, returnType, params);
	}

	public static ReflectInvoker getReflectInvoker(Class<?> clazz, String fieldName) throws NoSuchFieldException, ReflectionGenericException
	{
		return checkFieldAndGeneric(clazz.getClassLoader(), clazz, fieldName, false);
	}

	public static ReflectInvoker getReflectInvoker(ClassLoader loader, String className, String fieldName) throws NoSuchFieldException, ReflectionGenericException, ClassNotFoundException
	{
		Class<?> checkClass = Class.forName(className, false, loader);
		return checkFieldAndGeneric(loader, checkClass, fieldName, false);
	}

	public static ReflectInvoker getReflectInvoker(Class<?> clazz, String fieldName, boolean deepReflect) throws NoSuchFieldException, ReflectionGenericException
	{
		return checkFieldAndGeneric(clazz.getClassLoader(), clazz, fieldName, deepReflect);
	}

	public static ReflectInvoker getReflectInvoker(ClassLoader loader, String className, String fieldName, boolean deepReflect) throws NoSuchFieldException, ReflectionGenericException, ClassNotFoundException
	{
		Class<?> checkClass = Class.forName(className, false, loader);
		return checkFieldAndGeneric(loader, checkClass, fieldName, deepReflect);
	}

	private static ReflectInvoker checkMethodAndGeneric(ClassLoader loader, Class<?> clazz, String methodName, Class<?> returnType, Class<?>... params) throws NoSuchMethodException, ReflectionGenericException
	{
		String descName = clazz.getTypeName().replace('.', '/');
		String pathName = descName.concat(".class");
		URL url = loader.getResource(pathName);
		if (url == null) throw new NullPointerException(pathName);
		MethodType type = MethodType.methodType(returnType, params);
		ClassMethod method;
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
				if (
					Objects.requireNonNull(methodName).equals(((ConstantUTF8)pool.getConstantPoolElement(method.getNameIndex())).getUTF8()) &&
					MethodType.fromMethodDescriptorString(
						((ConstantUTF8)pool.getConstantPoolElement(method.getDescriptorIndex())).getUTF8(),
						loader
					).equals(type)) return generic(clazz.getClassLoader(), file, method);
			}
			throw new NoSuchMethodException(descName+'.'+methodName+":"+type.toMethodDescriptorString());
		}
		catch (IOException e)
		{
			throw new ReflectionGenericException("Can not check method name", e);
		}
	}

	private static ReflectInvoker checkFieldAndGeneric(ClassLoader loader, Class<?> clazz, String fieldName, boolean deepReflect) throws NoSuchFieldException, ReflectionGenericException
	{
		String descName = clazz.getTypeName().replace('.', '/');
		String pathName = descName.concat(".class");
		URL url = loader.getResource(pathName);
		if (url == null) throw new NullPointerException(pathName);
		ClassField field;
		try
		{
			InputStream in = url.openStream();
			byte[] code = IO.toByteArray(in);
			in.close();
			ClassFile file = new ClassFile(code);
			ConstantPool pool = file.getConstantPool();
			for (int i = 0; i < file.getFieldCount(); i++)
			{
				field = file.getField(i);
				if (Objects.requireNonNull(fieldName).equals(((ConstantUTF8)pool.getConstantPoolElement(field.getNameIndex())).getUTF8())) return generic(loader, file, field, deepReflect);
			}
			throw new NoSuchFieldException(descName+"."+fieldName);
		}
		catch (IOException e)
		{
			throw new ReflectionGenericException("Can not check field name", e);
		}
	}

	private static ReflectInvoker generic(ClassLoader loader, ClassFile clazz, ClassMethod method) throws ReflectionGenericException
	{
		ConstantPool pool = clazz.getConstantPool();
		// class name
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		// Method name
		String methodName = ((ConstantUTF8)pool.getConstantPoolElement(method.getNameIndex())).getUTF8();
		// description of the method
		String desc = ((ConstantUTF8)pool.getConstantPoolElement(method.getDescriptorIndex())).getUTF8();
		// static
		final boolean isStatic = ((method.getAccessFlag() & AccessFlag.ACC_STATIC) != 0);
		// binary name of class
		final String owner = ((ConstantUTF8)pool.getConstantPoolElement(((ConstantClass)pool.getConstantPoolElement(clazz.getThisClassIndex())).getNameIndex())).getUTF8();
		// method descriptor to MethodType
		MethodType type = MethodType.fromMethodDescriptorString(desc, loader);
		//return type
		Class<?> returnType = type.returnType();
		// params
		Class<?>[] params = type.parameterArray();
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
		mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS, "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitCode();
		if (!isStatic) mv.visitVarInsn(Opcodes.ALOAD, 1);
		int i=0;
		for (Class<?> c : type.parameterArray())
		{
			mv.visitVarInsn(Opcodes.ALOAD, 2);
//			mv.visitVarInsn(Opcodes.BIPUSH, i+10);
			mv.visitIntInsn(Opcodes.BIPUSH, i++);
			mv.visitInsn(Opcodes.AALOAD);
			if (c.isPrimitive())
			{
				if (c == byte.class)mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "byteValue", "()B", false);
				else if (c == short.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "shortValue", "()S", false);
				else if (c == int.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
				else if (c == long.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J", false);
				else if (c == float.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", false);
				else if (c == double.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
				else if (c == boolean.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "booleanValue", "()Z", false);
				else if (c == char.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "charValue", "()C", false);
			}
		}
		if (isStatic) mv.visitMethodInsn(Opcodes.INVOKESTATIC, owner, methodName, desc, false);
		else mv.visitMethodInsn(methodName.equals("<init>") ? Opcodes.INVOKESPECIAL : Opcodes.INVOKEVIRTUAL, owner, methodName, desc, false);
		if (returnType == void.class) mv.visitInsn(Opcodes.RETURN);
		else
		{
			if (returnType == byte.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
			else if (returnType == short.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
			else if (returnType == int.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			else if (returnType == long.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
			else if (returnType == float.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
			else if (returnType == double.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
			else if (returnType == boolean.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
			else if (returnType == char.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
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

	private static ReflectInvoker generic(ClassLoader loader, ClassFile clazz, ClassField field, boolean deepReflect) throws ReflectionGenericException
	{
		ConstantPool pool = clazz.getConstantPool();
		// class name
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;

		String name = ((ConstantUTF8)pool.getConstantPoolElement(field.getNameIndex())).getUTF8();
		String desc = ((ConstantUTF8)pool.getConstantPoolElement(field.getDescriptorIndex())).getUTF8();
		String owner = ((ConstantUTF8)pool.getConstantPoolElement(((ConstantClass)pool.getConstantPoolElement(clazz.getThisClassIndex())).getNameIndex())).getUTF8();

		boolean isStatic = (field.getAccessFlag() & AccessFlag.ACC_STATIC) != 0;
		boolean isFinal = (field.getAccessFlag() & AccessFlag.ACC_FINAL) != 0;

		Class<?> type = MethodType.fromMethodDescriptorString("()"+desc, loader).unwrap().returnType();

		int stack = isFinal ? deepReflect ? 5 : 3 : isStatic ? 3 : 2;
//		int locals = isFinal && deepReflect && isStatic ? 4 : 3;
		int locals = 3;

		ClassWriter cw = new ClassWriter(0);
		cw.visit(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, null, SUPER_CLASS, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, SUPER_CLASS, "<init>", "()V", false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitInsn(Opcodes.AALOAD);
		Label label = new Label();
		mv.visitJumpInsn(Opcodes.IFNULL, label);
		if (isFinal)
		{
			if (deepReflect)
			{
				try
				{
					Class<?> oc = Class.forName(owner.replace('/', '.'));
					Field field1 = oc.getDeclaredField(name);
					long offset = isStatic ? USF.staticFieldOffset(field1) : USF.objectFieldOffset(field1);
//					if (isStatic)
//					{
//						mv.visitLdcInsn(oc);
//						mv.visitVarInsn(Opcodes.ASTORE, 3);
//					}
					mv.visitFieldInsn(Opcodes.GETSTATIC, "sun/misc/Unsafe", "theUnsafe", "Lsun/misc/Unsafe;");
//					mv.visitVarInsn(Opcodes.ALOAD, isStatic ? 3 : 1);
					if (isStatic) mv.visitLdcInsn(Type.getType(oc));
					else mv.visitVarInsn(Opcodes.ALOAD, 1);
					mv.visitLdcInsn(offset);
					mv.visitVarInsn(Opcodes.ALOAD, 2);
					mv.visitInsn(Opcodes.ICONST_0);
					mv.visitInsn(Opcodes.AALOAD);
					if (type.isPrimitive())
					{
						if (type == byte.class)
						{
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "byteValue", "()B", false);
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putByteVolatile", "(Ljava/lang/Object;JB)V", false);
						}
						else if (type == short.class)
						{
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "shortValue", "()S", false);
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putShortVolatile", "(Ljava/lang/Object;JS)V", false);
						}
						else if (type == int.class)
						{
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putIntVolatile", "(Ljava/lang/Object;JI)V", false);
						}
						else if (type == long.class)
						{
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J", false);
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putLongVolatile", "(Ljava/lang/Object;JJ)V", false);
						}
						else if (type == float.class)
						{
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", false);
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putFloatVolatile", "(Ljava/lang/Object;JF)V", false);
						}
						else if (type == double.class)
						{
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putDoubleVolatile", "(Ljava/lang/Object;JD)V", false);
						}
						else if (type == boolean.class)
						{
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "booleanValue", "()Z", false);
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putBooleanVolatile", "(Ljava/lang/Object;JZ)V", false);
						}
						else if (type == char.class)
						{
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "charValue", "()C", false);
							mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putCharVolatile", "(Ljava/lang/Object;JC)V", false);
						}
					}
					else mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", false);
				}
				catch (Throwable t)
				{
					throw new ReflectionGenericException("Can not generic invoker", t);
				}
			}
			else
			{
				mv.visitTypeInsn(Opcodes.NEW, "org/mve/util/reflect/IllegalOperationException");
				mv.visitInsn(Opcodes.DUP);
				mv.visitLdcInsn("Field is final");
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/mve/util/reflect/IllegalOperationException", "<init>", "(Ljava/lang/String;)V", false);
				mv.visitInsn(Opcodes.ATHROW);
			}
		}
		else
		{
			if (!isStatic) mv.visitVarInsn(Opcodes.ALOAD, 1);
			mv.visitVarInsn(Opcodes.ALOAD, 2);
			mv.visitInsn(Opcodes.ICONST_0);
			mv.visitInsn(Opcodes.AALOAD);
			if (type.isPrimitive())
			{
				if (type == byte.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "byteValue", "()B", false);
				else if (type == short.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "shortValue", "()S", false);
				else if (type == int.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
				else if (type == long.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J", false);
				else if (type == float.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", false);
				else if (type == double.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
				else if (type == boolean.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "booleanValue", "()Z", false);
				else if (type == char.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "charValue", "()C", false);
			}
			mv.visitFieldInsn(isStatic ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD, owner, name, desc);
		}
		mv.visitLabel(label);
		if (!isStatic) mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitFieldInsn(isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD, owner, name, desc);
		if (type.isPrimitive())
		{
			if (type == byte.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
			else if (type == short.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
			else if (type == int.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			else if (type == long.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
			else if (type == float.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
			else if (type == double.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
			else if (type == boolean.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
			else if (type == char.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
		}
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(stack, locals);
		mv.visitEnd();
		cw.visitEnd();
		byte[] code = cw.toByteArray();

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

	static
	{
		MethodHandle handle = null;
		String superClass = null;
		Unsafe usf = null;
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
			usf = (Unsafe) field.get(null);

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
		USF = usf;
		DEFINE = handle;
		SUPER_CLASS = superClass;
	}
}
