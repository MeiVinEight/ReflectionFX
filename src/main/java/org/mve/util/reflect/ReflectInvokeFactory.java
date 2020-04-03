package org.mve.util.reflect;

import org.jetbrains.org.objectweb.asm.ClassWriter;
import org.jetbrains.org.objectweb.asm.FieldVisitor;
import org.jetbrains.org.objectweb.asm.Label;
import org.jetbrains.org.objectweb.asm.MethodVisitor;
import org.jetbrains.org.objectweb.asm.Opcodes;
import org.jetbrains.org.objectweb.asm.Type;
import org.mve.util.asm.OperandStack;
import org.mve.util.asm.file.AccessFlag;
import sun.misc.Unsafe;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class ReflectInvokeFactory
{
	public static final Unsafe UNSAFE;
	public static final MethodHandles.Lookup TRUSTED_LOOKUP;
	public static final Class<?> DELEGATING_CLASS;
//	public static final MethodHandle DEFINE;
	public static final ReflectInvoker<Object> METHOD_HANDLE_INVOKER;
	private static final ReflectInvoker<Class<?>> DEFINE;
	private static final Class<?> LOADER_CLASS;
	private static final String SUPER_CLASS;
	private static final ClassLoader INTERNAL_CLASS_LOADER;
	private static int id = 0;

	public static <T> ReflectInvoker<T> getReflectInvoker(Class<?> clazz, String methodName, boolean isStatic, Class<T> returnType, Class<?>... params) throws ReflectionGenericException
	{
		try { return generic(clazz, methodName, MethodType.methodType(returnType, params), isStatic); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectInvoker<T> getReflectInvoker(Class<?> clazz, String fieldName, Class<T> type, boolean isStatic, boolean isFinal)
	{
		try { return generic(clazz, fieldName, type, isStatic, isFinal, false); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectInvoker<T> getReflectInvoker(Class<?> clazz, String fieldName, Class<T> type, boolean isStatic, boolean isFinal, boolean deepReflect)
	{
		try { return generic(clazz, fieldName, type, isStatic, isFinal, deepReflect); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectInvoker<T> getReflectInvoker(Class<T> clazz)
	{
		try { return generic(clazz); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectInvoker<T> getReflectInvoker(Class<T> clazz, boolean initialize)
	{
		try { return initialize ? generic(clazz, MethodType.methodType(void.class)) : generic(clazz); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectInvoker<T> getReflectInvoker(Class<T> clazz, Class<?>... params)
	{
		try { return generic(clazz, MethodType.methodType(void.class, params)); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker<Void> throwException()
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<Ljava/lang/Void;>;", "java/lang/Object", new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, "java/lang/Object");
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Void;", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitInsn(Opcodes.AALOAD);
		mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Throwable");
		mv.visitInsn(Opcodes.ATHROW);
		mv.visitMaxs(2, 2);
		bridge(cw, className, Void.class);
		try { return define(cw, ClassLoader.getSystemClassLoader()); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectInvoker<T> constant(T value)
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<"+getDescriptor(value.getClass())+">;", "java/lang/Object", new String[]{"org/mve/util/reflect/ReflectInvoker"});
		FieldVisitor fv = cw.visitField(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_FINAL, "final", getDescriptor(value.getClass()), null, null);
		fv.visitEnd();
		MethodVisitor mv = cw.visitMethod(
			AccessFlag.ACC_PUBLIC,
			"<init>",
			MethodType.methodType(void.class, value.getClass()).toMethodDescriptorString(),
			null,
			null
		);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, className, "final", getDescriptor(value.getClass()));
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
		mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)"+getDescriptor(value.getClass()), null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, className, "final", getDescriptor(value.getClass()));
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(1, 2);
		mv.visitEnd();
		bridge(cw, className, value.getClass());
		cw.visitEnd();
		byte[] code = cw.toByteArray();
		try
		{
			Class<?> clazz = DEFINE.invoke(INTERNAL_CLASS_LOADER, code);
			return (ReflectInvoker<T>) clazz.getDeclaredConstructor(value.getClass()).newInstance(value);
		}
		catch (Throwable throwable)
		{
			throw new ReflectionGenericException("Can not generic invoker", throwable);
		}
	}

	private static <T> ReflectInvoker<T> generic(Class<?> clazz, String methodName, MethodType type, boolean isStatic) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		String desc = type.toMethodDescriptorString();
		final String owner = clazz.getTypeName().replace('.', '/');
		Class<?> returnType = type.returnType();
		Class<?>[] params = type.parameterArray();
		final OperandStack stack = new OperandStack();
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<"+getDescriptor(returnType == void.class ? Void.class : returnType)+">;", SUPER_CLASS, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, SUPER_CLASS);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)"+getDescriptor(returnType == void.class ? Void.class : returnType), null, null);
		mv.visitCode();
		if (!isStatic) arrayFirst(mv, owner, stack);
		pushArguments(params, mv, isStatic ? 0 : 1, stack);
		if (isStatic) mv.visitMethodInsn(Opcodes.INVOKESTATIC, owner, methodName, desc, false);
		else mv.visitMethodInsn(methodName.equals("<init>") ? Opcodes.INVOKESPECIAL : Opcodes.INVOKEVIRTUAL, owner, methodName, desc, false);
		for (int i = 0; i < params.length; i++) stack.pop();
		if (!isStatic) stack.pop();
		if (returnType == void.class) { mv.visitInsn(Opcodes.ACONST_NULL); stack.push(); }
		else warp(returnType, mv);
		mv.visitInsn(Opcodes.ARETURN);
		stack.pop();
		mv.visitMaxs(stack.getMaxSize(), 2);
		mv.visitEnd();
		bridge(cw, className, returnType == void.class ? Void.class : returnType);
		return define(cw, INTERNAL_CLASS_LOADER);
	}

	private static <T> ReflectInvoker<T> generic(Class<?> clazz, String fieldName, Class<T> type, boolean isStatic, boolean isFinal, boolean deepReflect) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		String desc = getDescriptor(type);
		String owner = clazz.getTypeName().replace('.', '/');
		final OperandStack stack = new OperandStack();
		ClassWriter cw = new ClassWriter(0);
		cw.visit(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<"+getDescriptor(type)+">;", SUPER_CLASS, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, SUPER_CLASS);
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)"+getDescriptor(type), null, null);
		mv.visitCode();
		Label label = new Label();
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		stack.push();
		mv.visitJumpInsn(Opcodes.IFNULL, label);
		stack.pop();
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		stack.push();
		mv.visitInsn(Opcodes.ARRAYLENGTH);
		mv.visitInsn(isStatic ? Opcodes.ICONST_1 : Opcodes.ICONST_2);
		stack.push();
		mv.visitJumpInsn(Opcodes.IF_ICMPLT, label);
		stack.pop();
		stack.pop();
		if (type.isPrimitive())
		{
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			stack.push();
			mv.visitInsn(isStatic ? Opcodes.ICONST_0 : Opcodes.ICONST_1);
			stack.push();
			mv.visitInsn(Opcodes.AALOAD);
			stack.pop();
			mv.visitJumpInsn(Opcodes.IFNULL, label);
			stack.pop();
		}
		if (isFinal)
		{
			if (deepReflect)
			{
				try
				{
					Field field1 = clazz.getDeclaredField(fieldName);
					long offset = isStatic ? UNSAFE.staticFieldOffset(field1) : UNSAFE.objectFieldOffset(field1);
					mv.visitFieldInsn(Opcodes.GETSTATIC, "sun/misc/Unsafe", "theUnsafe", "Lsun/misc/Unsafe;");
					stack.push();
					if (isStatic) { mv.visitLdcInsn(Type.getType(clazz)); stack.push(); }
					else arrayFirst(mv, owner, stack);
					mv.visitLdcInsn(offset);
					stack.push();
					mv.visitVarInsn(Opcodes.ALOAD, 1);
					stack.push();
					mv.visitInsn(isStatic ? Opcodes.ICONST_0 : Opcodes.ICONST_1);
					stack.push();
					mv.visitInsn(Opcodes.AALOAD);
					stack.pop();
					if (type.isPrimitive())
					{
						unwarp(type, mv);
						if (type == byte.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putByteVolatile", "(Ljava/lang/Object;JB)V", false);
						else if (type == short.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putShortVolatile", "(Ljava/lang/Object;JS)V", false);
						else if (type == int.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putIntVolatile", "(Ljava/lang/Object;JI)V", false);
						else if (type == long.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putLongVolatile", "(Ljava/lang/Object;JJ)V", false);
						else if (type == float.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putFloatVolatile", "(Ljava/lang/Object;JF)V", false);
						else if (type == double.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putDoubleVolatile", "(Ljava/lang/Object;JD)V", false);
						else if (type == boolean.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putBooleanVolatile", "(Ljava/lang/Object;JZ)V", false);
						else if (type == char.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putCharVolatile", "(Ljava/lang/Object;JC)V", false);
					}
					else mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", false);
					stack.pop();
					stack.pop();
					stack.pop();
					stack.pop();
				}
				catch (Throwable t)
				{
					throw new ReflectionGenericException("Can not generic invoker", t);
				}
			}
			else
			{
				mv.visitTypeInsn(Opcodes.NEW, "org/mve/util/reflect/IllegalOperationException");
				stack.push();
				mv.visitInsn(Opcodes.DUP);
				stack.push();
				mv.visitLdcInsn("Field is final");
				stack.push();
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/mve/util/reflect/IllegalOperationException", "<init>", "(Ljava/lang/String;)V", false);
				stack.pop();
				stack.pop();
				mv.visitInsn(Opcodes.ATHROW);
				stack.pop();
			}
		}
		else
		{
			if (!isStatic) arrayFirst(mv, owner, stack);
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			stack.push();
			mv.visitInsn(isStatic ? Opcodes.ICONST_0 : Opcodes.ICONST_1);
			stack.push();
			mv.visitInsn(Opcodes.AALOAD);
			stack.pop();
			if (type.isPrimitive()) unwarp(type, mv);
			else mv.visitTypeInsn(Opcodes.CHECKCAST, type.getTypeName().replace('.', '/'));
			mv.visitFieldInsn(isStatic ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD, owner, fieldName, desc);
			stack.pop();
			if (!isStatic) stack.pop();
		}
		mv.visitLabel(label);
		if (!isStatic) arrayFirst(mv, owner, stack);
		mv.visitFieldInsn(isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD, owner, fieldName, desc);
		if (isStatic) stack.push();
		if (type.isPrimitive()) warp(type, mv);
		mv.visitInsn(Opcodes.ARETURN);
		stack.pop();
		mv.visitMaxs(stack.getMaxSize(), 2);
		mv.visitEnd();
		bridge(cw, className, type);
		return define(cw, INTERNAL_CLASS_LOADER);
	}

	private static <T> ReflectInvoker<T> generic(Class<T> clazz, MethodType type) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		String desc = type.toMethodDescriptorString();
		final OperandStack stack = new OperandStack();
		String owner = clazz.getTypeName().replace('.', '/');
		ClassWriter cw = new ClassWriter(0);
		cw.visit(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<"+getDescriptor(clazz)+">;", SUPER_CLASS, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, SUPER_CLASS);
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC, "invoke", "([Ljava/lang/Object;)"+getDescriptor(clazz), null, null);
		mv.visitCode();
		mv.visitTypeInsn(Opcodes.NEW, owner);
		stack.push();
		mv.visitInsn(Opcodes.DUP);
		stack.push();
		pushArguments(type.parameterArray(), mv, 0, stack);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, "<init>", desc, false);
		for (int i = 0; i < type.parameterArray().length; i++) stack.pop();
		stack.pop();
		mv.visitInsn(Opcodes.ARETURN);
		stack.pop();
		mv.visitMaxs(stack.getMaxSize(), 2);
		mv.visitEnd();
		bridge(cw, className, clazz);
		return define(cw, INTERNAL_CLASS_LOADER);
	}

	private static <T> ReflectInvoker<T> generic(Class<T> clazz) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, AccessFlag.ACC_STRICT | AccessFlag.ACC_PUBLIC, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<"+getDescriptor(clazz)+">;", SUPER_CLASS, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, SUPER_CLASS);
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)"+getDescriptor(clazz), null, null);
		mv.visitTypeInsn(Opcodes.NEW, clazz.getTypeName().replace('.', '/'));
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(1, 2);
		mv.visitEnd();
		bridge(cw, className, clazz);
		return define(cw, ClassLoader.getSystemClassLoader().getParent());
	}

	private static void bridge(ClassWriter cw, String className, Class<?> returnType)
	{
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_BRIDGE | AccessFlag.ACC_SYNTHETIC, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, "invoke", "([Ljava/lang/Object;)"+getDescriptor(returnType), false);
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

	private static void arrayFirst(MethodVisitor mv, String type, OperandStack stack)
	{
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		stack.push();
		mv.visitInsn(Opcodes.ICONST_0);
		stack.push();
		mv.visitInsn(Opcodes.AALOAD);
		stack.pop();
		mv.visitTypeInsn(Opcodes.CHECKCAST, type);
	}

	private static void pushArguments(Class<?>[] arguments, MethodVisitor mv, int start, OperandStack stack)
	{
		for (Class<?> c : arguments)
		{
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			stack.push();
			mv.visitIntInsn(Opcodes.BIPUSH, start++);
			stack.push();
			mv.visitInsn(Opcodes.AALOAD);
			stack.pop();
			if (c.isPrimitive()) unwarp(c, mv);
			else mv.visitTypeInsn(Opcodes.CHECKCAST, getType(c));
		}
	}

	private static void warp(Class<?> c, MethodVisitor mv)
	{
		if (c == byte.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
		else if (c == short.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
		else if (c == int.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		else if (c == long.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
		else if (c == float.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
		else if (c == double.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
		else if (c == boolean.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
		else if (c == char.class) mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
	}

	private static void unwarp(Class<?> c, MethodVisitor mv)
	{
		mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Number");
		if (c == byte.class)mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "byteValue", "()B", false);
		else if (c == short.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "shortValue", "()S", false);
		else if (c == int.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
		else if (c == long.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J", false);
		else if (c == float.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", false);
		else if (c == double.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
		else if (c == boolean.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "booleanValue", "()Z", false);
		else if (c == char.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "charValue", "()C", false);
	}

	private static <T> ReflectInvoker<T> define(ClassWriter cw, ClassLoader loader) throws Throwable
	{
		cw.visitEnd();
		byte[] code = cw.toByteArray();
		System.out.println(loader);
		Class<?> implClass = DEFINE.invoke(loader, code);
		TRUSTED_LOOKUP.findVirtual(ClassLoader.class, "resolveClass", MethodType.methodType(void.class, Class.class)).invoke(loader, implClass);
		System.out.println(implClass.getClassLoader().loadClass(implClass.getTypeName()));
		return (ReflectInvoker<T>) implClass.getDeclaredConstructor().newInstance();
	}

	private static void genericConstructor(ClassWriter cw, String superClass)
	{
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClass, "<init>", "()V", false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	private static String getType(Class<?> clazz)
	{
		StringBuilder sb = new StringBuilder();
		while (clazz.isArray())
		{
			sb.append('[');
			clazz = clazz.getComponentType();
		}
		if (clazz.isPrimitive()) sb.append(primitive(clazz));
		else sb.append(clazz.getTypeName().replace('.', '/'));
		return sb.toString();
	}

	private static String getDescriptor(Class<?> clazz)
	{
		StringBuilder builder = new StringBuilder();
		while (clazz.isArray())
		{
			builder.append('[');
			clazz = clazz.getComponentType();
		}
		if (clazz.isPrimitive()) builder.append(primitive(clazz));
		else builder.append('L').append(clazz.getTypeName().replace('.', '/')).append(';');
		return builder.toString();
	}

	private static String primitive(Class<?> clazz)
	{
		if (clazz == byte.class) return "B";
		else if (clazz == short.class) return "S";
		else if (clazz == int.class) return "I";
		else if (clazz == long.class) return "J";
		else if (clazz == float.class) return "F";
		else if (clazz == double.class) return "D";
		else if (clazz == boolean.class) return "Z";
		else if (clazz == char.class) return "C";
		else return null;
	}

	static
	{
		try
		{
			URL url = ClassLoader.getSystemClassLoader().getResource("java/lang/Object.class");
			if (url == null) throw new NullPointerException();
			InputStream in = url.openStream();
			if (6 != in.skip(6)) throw new UnknownError();
//			byte[] code = IO.toByteArray(in);
			int majorVersion = new DataInputStream(in).readShort() & 0XFFFF;
			in.close();

			{
				Field field = Unsafe.class.getDeclaredField("theUnsafe");
				field.setAccessible(true);
				UNSAFE = (Unsafe) field.get(null);

				if (majorVersion > 0X34)
				{
					Class<?> clazz = Class.forName("jdk.internal.module.IllegalAccessLogger");
					Field loggerField = clazz.getDeclaredField("logger");
					long offset = UNSAFE.staticFieldOffset(loggerField);
					UNSAFE.putObjectVolatile(clazz, offset, null);
				}

				if (majorVersion <= 0X34) SUPER_CLASS = "sun/reflect/MagicAccessorImpl";
				else SUPER_CLASS = "jdk/internal/reflect/MagicAccessorImpl";

			}

			Class<?> clazz = DELEGATING_CLASS = Class.forName(majorVersion > 0x34 ? "jdk.internal.reflect.DelegatingClassLoader" : "sun.reflect.DelegatingClassLoader");
			Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
			field.setAccessible(true);
			MethodHandles.Lookup lookup = TRUSTED_LOOKUP = (MethodHandles.Lookup) field.get(null);
//			INTERNAL_CLASS_LOADER = (ClassLoader) lookup.findConstructor(clazz, MethodType.methodType(void.class, ClassLoader.class)).invoke(ClassLoader.getSystemClassLoader());
			MethodHandle define = lookup.findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class));

			Class<?> handleInvoker;
			try
			{
				handleInvoker = Class.forName("org.mve.util.reflect.MethodHandleInvoker");
			}
			catch (Throwable t)
			{
				ClassWriter cw = new ClassWriter(0);
				cw.visit(52, AccessFlag.ACC_SUPER | AccessFlag.ACC_PUBLIC, "org/mve/util/reflect/MethodHandleInvoker", "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<Ljava/lang/Class;>;", "java/lang/Object", new String[]{"org/mve/util/reflect/ReflectInvoker"});
				genericConstructor(cw, "java/lang/Object");
				MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitInsn(Opcodes.ICONST_0);
				mv.visitInsn(Opcodes.AALOAD);
				mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/invoke/MethodHandle");
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitInsn(Opcodes.ICONST_1);
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitInsn(Opcodes.ARRAYLENGTH);
				mv.visitMethodInsn(
					Opcodes.INVOKESTATIC,
					Arrays
						.class
						.getTypeName()
						.replace('.', '/'),
					"copyOfRange",
					MethodType.methodType(
						Object[].class,
						Object[].class,
						int.class,
						int.class
					).toMethodDescriptorString(),
					false
				);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, MethodHandle.class.getTypeName().replace('.', '/'), "invokeWithArguments", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false);
				mv.visitInsn(Opcodes.ARETURN);
				mv.visitMaxs(4, 2);
				mv.visitEnd();
				cw.visitEnd();
				byte[] code = cw.toByteArray();
				handleInvoker = (Class<?>) define.invoke(ClassLoader.getSystemClassLoader(), null, code, 0, code.length);
			}
//			if (handleInvoker == null) throw new UnknownError();
			METHOD_HANDLE_INVOKER = (ReflectInvoker<Object>) handleInvoker.getDeclaredConstructor().newInstance();

			{
				Class<?> loaderClass;
				try
				{
					loaderClass = Class.forName(DELEGATING_CLASS.getPackage().getName().concat(".ReflectionClassLoader"));
				}
				catch (Throwable t)
				{
					String name = DELEGATING_CLASS.getPackage().getName().replace('.', '/').concat("/ReflectionClassLoader");
					ClassWriter cw = new ClassWriter(0);
					cw.visit(
						52,
						AccessFlag.ACC_SUPER | AccessFlag.ACC_PUBLIC,
						name,
						null,
						DELEGATING_CLASS.getTypeName().replace('.', '/'),
						null
					);
					FieldVisitor fv = cw.visitField(
						AccessFlag.ACC_PRIVATE | AccessFlag.ACC_FINAL | AccessFlag.ACC_SYNTHETIC,
						"class",
						getDescriptor(Map.class),
						"L".concat(getType(Map.class)).concat("<").concat(getDescriptor(String.class)).concat("L").concat(getType(Class.class)).concat("<*>;>;"),
						null
					);
					fv.visitEnd();

					MethodVisitor mv = cw.visitMethod(
						AccessFlag.ACC_PUBLIC,
						"<init>",
						MethodType.methodType(void.class, ClassLoader.class).toMethodDescriptorString(),
						null,
						null
					);
					mv.visitCode();
					mv.visitVarInsn(Opcodes.ALOAD, 0);
					mv.visitVarInsn(Opcodes.ALOAD, 1);
					mv.visitMethodInsn(
						Opcodes.INVOKESPECIAL,
						DELEGATING_CLASS
							.getTypeName()
							.replace('.', '/'),
						"<init>",
						MethodType.methodType(
							void.class,
							ClassLoader.class
						).toMethodDescriptorString(),
						false
					);
					mv.visitVarInsn(Opcodes.ALOAD, 0);
					mv.visitTypeInsn(Opcodes.NEW, ConcurrentHashMap.class.getTypeName().replace('.', '/'));
					mv.visitInsn(Opcodes.DUP);
					mv.visitMethodInsn(Opcodes.INVOKESPECIAL, ConcurrentHashMap.class.getTypeName().replace('.', '/'), "<init>", "()V", false);
					mv.visitFieldInsn(Opcodes.PUTFIELD, name, "class", getDescriptor(Map.class));
					mv.visitInsn(Opcodes.RETURN);
					mv.visitMaxs(3, 2);
					mv.visitEnd();
					mv = cw.visitMethod(
						AccessFlag.ACC_PUBLIC,
						"define",
						MethodType.methodType(Class.class, byte[].class).toMethodDescriptorString(),
						"(".concat(getDescriptor(byte[].class)).concat(")L").concat(getType(Class.class)).concat("<*>;"),
						null
					);
					mv.visitCode();
					mv.visitVarInsn(Opcodes.ALOAD, 0);
					mv.visitInsn(Opcodes.ACONST_NULL);
					mv.visitVarInsn(Opcodes.ALOAD, 1);
					mv.visitInsn(Opcodes.ICONST_0);
					mv.visitVarInsn(Opcodes.ALOAD, 1);
					mv.visitInsn(Opcodes.ARRAYLENGTH);
					mv.visitMethodInsn(
						Opcodes.INVOKEVIRTUAL,
						getType(ClassLoader.class),
						"defineClass",
						MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class).toMethodDescriptorString(),
						false
					);
					mv.visitVarInsn(Opcodes.ASTORE, 2);
					mv.visitVarInsn(Opcodes.ALOAD, 0);
					mv.visitFieldInsn(
						Opcodes.GETFIELD,
						name,
						"class",
						getDescriptor(Map.class)
					);
					mv.visitVarInsn(Opcodes.ALOAD, 2);
					mv.visitMethodInsn(
						Opcodes.INVOKEVIRTUAL,
						getType(Class.class),
						"getTypeName",
						MethodType.methodType(
							String.class
						).toMethodDescriptorString(),
						false
					);
					mv.visitVarInsn(Opcodes.ALOAD, 2);
					mv.visitMethodInsn(
						Opcodes.INVOKEINTERFACE,
						getType(Map.class),
						"put",
						MethodType.methodType(
							Object.class,
							Object.class,
							Object.class
						).toMethodDescriptorString(),
						true
					);
					mv.visitInsn(Opcodes.POP);
					mv.visitVarInsn(Opcodes.ALOAD, 2);
					mv.visitInsn(Opcodes.ARETURN);
					mv.visitMaxs(5, 3);
					mv.visitEnd();

					mv = cw.visitMethod(
						AccessFlag.ACC_PUBLIC,
						"findClass",
						MethodType.methodType(
							Class.class,
							String.class
						).toMethodDescriptorString(),
						"(".concat(getDescriptor(String.class)).concat(")L").concat(getType(Class.class)).concat("<*>;"),
						null
					);
					mv.visitCode();
					mv.visitVarInsn(Opcodes.ALOAD, 0);
					mv.visitFieldInsn(Opcodes.GETFIELD, name, "class", getDescriptor(Map.class));
					mv.visitVarInsn(Opcodes.ALOAD, 1);
					mv.visitMethodInsn(
						Opcodes.INVOKEINTERFACE,
						getType(Map.class),
						"get",
						MethodType.methodType(
							Object.class,
							Object.class
						).toMethodDescriptorString(),
						true
					);
					mv.visitTypeInsn(Opcodes.CHECKCAST, getType(Class.class));
					mv.visitVarInsn(Opcodes.ASTORE, 2);
					mv.visitVarInsn(Opcodes.ALOAD, 2);
					Label l = new Label();
					mv.visitJumpInsn(Opcodes.IFNONNULL, l);
					mv.visitVarInsn(Opcodes.ALOAD, 0);
					mv.visitVarInsn(Opcodes.ALOAD, 1);
					mv.visitMethodInsn(
						Opcodes.INVOKESPECIAL,
						getType(ClassLoader.class),
						"findClass",
						MethodType.methodType(
							Class.class,
							String.class
						).toMethodDescriptorString(),
						false
					);
					mv.visitVarInsn(Opcodes.ASTORE, 2);
					mv.visitLabel(l);
					mv.visitVarInsn(Opcodes.ALOAD, 2);
					mv.visitInsn(Opcodes.ARETURN);
					mv.visitMaxs(2, 3);
					mv.visitEnd();

					mv = cw.visitMethod(
						AccessFlag.ACC_PUBLIC,
						"loadClass",
						MethodType.methodType(
							Class.class,
							String.class,
							boolean.class
						).toMethodDescriptorString(),
						"("
							.concat(getDescriptor(String.class))
							.concat(getDescriptor(boolean.class))
							.concat(")L")
							.concat(getType(Class.class))
							.concat("<*>;"),
						null
					);
					mv.visitCode();
					mv.visitFieldInsn(
						Opcodes.GETSTATIC,
						getType(System.class),
						"out",
						getDescriptor(PrintStream.class)
					);
					mv.visitVarInsn(Opcodes.ALOAD, 1);
					mv.visitMethodInsn(
						Opcodes.INVOKEVIRTUAL,
						getType(PrintStream.class),
						"println",
						MethodType.methodType(
							void.class,
							String.class
						).toMethodDescriptorString(),
						false
					);
					mv.visitFieldInsn(
						Opcodes.GETSTATIC,
						getType(System.class),
						"out",
						getDescriptor(PrintStream.class)
					);
					mv.visitVarInsn(Opcodes.ALOAD, 0);
					mv.visitFieldInsn(Opcodes.GETFIELD, name, "class", getDescriptor(Map.class));
					mv.visitMethodInsn(
						Opcodes.INVOKEVIRTUAL,
						getType(PrintStream.class),
						"println",
						MethodType.methodType(
							void.class,
							Object.class
						).toMethodDescriptorString(),
						false
					);
					mv.visitVarInsn(Opcodes.ALOAD, 0);
					mv.visitVarInsn(Opcodes.ALOAD, 1);
					mv.visitVarInsn(Opcodes.ALOAD, 2);
					mv.visitMethodInsn(
						Opcodes.INVOKESPECIAL,
						getType(ClassLoader.class),
						"loadClass",
						MethodType.methodType(
							Class.class,
							String.class,
							boolean.class
						).toMethodDescriptorString(),
						false
					);
					mv.visitInsn(Opcodes.ARETURN);
					mv.visitMaxs(3, 3);
					mv.visitEnd();
					cw.visitEnd();

					byte[] code = cw.toByteArray();
					loaderClass = UNSAFE.defineClass(null, code, 0, code.length, null, null);
//					loaderClass = (Class<?>) METHOD_HANDLE_INVOKER.invoke(DEFINE, ClassLoader.getSystemClassLoader(), null, code, 0, code.length);
				}
				LOADER_CLASS = loaderClass;
				INTERNAL_CLASS_LOADER = (ClassLoader) LOADER_CLASS.getDeclaredConstructor(ClassLoader.class).newInstance(ClassLoader.getSystemClassLoader());

				ClassWriter cw = new ClassWriter(0);
				{
//					"Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<"+getDescriptor(value.getClass())+">;"
					cw.visit(
						0x34,
						AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER,
						"org/mve/util/reflect/DEFINER",
						getDescriptor(Object.class)
							.concat("L")
							.concat(getType(ReflectInvoker.class))
							.concat("<L")
							.concat(getType(Class.class))
							.concat("<*>;>;"),
						getType(Object.class),
						new String[]{getType(ReflectInvoker.class)}
					);
					genericConstructor(cw, getType(Object.class));
					MethodVisitor mv = cw.visitMethod(
						AccessFlag.ACC_PUBLIC,
						"invoke",
						MethodType.methodType(Class.class, Object[].class).toMethodDescriptorString(),
						"("
							.concat(getDescriptor(Object[].class))
							.concat(")L")
							.concat(getType(Class.class))
							.concat("<*>;"),
						null
					);
					mv.visitVarInsn(Opcodes.ALOAD, 1);
					mv.visitInsn(Opcodes.ICONST_0);
					mv.visitInsn(Opcodes.AALOAD);
					mv.visitTypeInsn(Opcodes.CHECKCAST, getType(LOADER_CLASS));
					mv.visitVarInsn(Opcodes.ALOAD, 1);
					mv.visitInsn(Opcodes.ICONST_1);
					mv.visitInsn(Opcodes.AALOAD);
					mv.visitTypeInsn(Opcodes.CHECKCAST, getType(byte[].class));
					mv.visitMethodInsn(
						Opcodes.INVOKEVIRTUAL,
						getType(LOADER_CLASS),
						"define",
						MethodType.methodType(
							Class.class,
							byte[].class
						).toMethodDescriptorString(),
						false
					);
					mv.visitInsn(Opcodes.ARETURN);
					mv.visitMaxs(3, 2);
					mv.visitEnd();
					bridge(cw, "org/mve/util/reflect/DEFINER", Class.class);
					cw.visitEnd();
				}
				byte[] code = cw.toByteArray();
//				clazz = (Class<?>) define.invoke(INTERNAL_CLASS_LOADER, null, code, 0, code.length);
				clazz = (Class<?>) TRUSTED_LOOKUP.findVirtual(LOADER_CLASS, "define", MethodType.methodType(Class.class, byte[].class)).invoke(INTERNAL_CLASS_LOADER, code);
				DEFINE = (ReflectInvoker<Class<?>>) clazz.getDeclaredConstructor().newInstance();
			}
		}
		catch (Throwable t)
		{
			throw new UninitializedException(t);
		}
	}
}
