package org.mve.util.reflect;

import org.jetbrains.org.objectweb.asm.ClassWriter;
import org.jetbrains.org.objectweb.asm.FieldVisitor;
import org.jetbrains.org.objectweb.asm.Label;
import org.jetbrains.org.objectweb.asm.MethodVisitor;
import org.jetbrains.org.objectweb.asm.ModuleVisitor;
import org.jetbrains.org.objectweb.asm.Opcodes;
import org.jetbrains.org.objectweb.asm.Type;
import org.mve.util.asm.OperandStack;
import org.mve.util.asm.file.AccessFlag;
import sun.misc.Unsafe;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unchecked", "unused"})
public class ReflectInvokeFactory
{
	public static final Unsafe UNSAFE;
	public static final MethodHandles.Lookup TRUSTED_LOOKUP;
	public static final Class<?> DELEGATING_CLASS;
	public static final MethodHandle DEFINE;
	public static final ReflectInvoker<Object> METHOD_HANDLE_INVOKER;
	public static final ReflectInvoker<Class<?>> CALLER = null; // FIXME
	private static final String MAGIC_ACCESSOR;
	private static final ReflectionClassLoader INTERNAL_CLASS_LOADER;
	private static final ReflectInvoker<ReflectionClassLoader> CLASS_LOADER_FACTORY;
	private static final Map<ClassLoader, ReflectionClassLoader> CLASS_LOADER_MAP = new ConcurrentHashMap<>();
	private static int id = 0;

	public static <T> ReflectInvoker<T> getReflectInvoker(Class<?> clazz, String methodName, boolean isStatic, Class<T> returnType, Class<?>... params) throws ReflectionGenericException
	{
		try { return clazz == ReflectInvokeFactory.class ? null : generic(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (CALLER.invoke()::getClassLoader)), clazz, methodName, MethodType.methodType(returnType, params), isStatic); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectInvoker<T> getReflectInvoker(Class<?> clazz, String fieldName, Class<T> type, boolean isStatic, boolean isFinal)
	{
		try { return clazz == ReflectInvokeFactory.class ? null : generic(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (CALLER.invoke()::getClassLoader)), clazz, fieldName, type, isStatic, isFinal, false); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectInvoker<T> getReflectInvoker(Class<?> clazz, String fieldName, Class<T> type, boolean isStatic, boolean isFinal, boolean deepReflect)
	{
		try { return clazz == ReflectInvokeFactory.class ? null : generic(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (CALLER.invoke()::getClassLoader)), clazz, fieldName, type, isStatic, isFinal, deepReflect); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectInvoker<T> getReflectInvoker(Class<T> clazz)
	{
		try { return generic(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (CALLER.invoke()::getClassLoader)), clazz); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectInvoker<T> getReflectInvoker(Class<T> clazz, boolean initialize)
	{
		try { return initialize ? generic(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (CALLER.invoke()::getClassLoader)), clazz, MethodType.methodType(void.class)) : generic(CALLER.invoke().getClassLoader(), clazz); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectInvoker<T> getReflectInvoker(Class<T> clazz, Class<?>... params)
	{
		try { return generic(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (CALLER.invoke()::getClassLoader)), clazz, MethodType.methodType(void.class, params)); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
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
		try { return define(cw, AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (CALLER.invoke()::getClassLoader))); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
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
			Class<?> clazz = getClassLoader(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (CALLER.invoke()::getClassLoader))).define(code);
			return (ReflectInvoker<T>) clazz.getDeclaredConstructor(value.getClass()).newInstance(value);
		}
		catch (Throwable throwable)
		{
			throw new ReflectionGenericException("Can not generic invoker", throwable);
		}
	}

	private static <T> ReflectInvoker<T> generic(ClassLoader callerLoader, Class<?> clazz, String methodName, MethodType type, boolean isStatic) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		String desc = type.toMethodDescriptorString();
		final String owner = clazz.getTypeName().replace('.', '/');
		Class<?> returnType = type.returnType();
		Class<?>[] params = type.parameterArray();
		final OperandStack stack = new OperandStack();
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<"+getDescriptor(returnType == void.class ? Void.class : returnType)+">;", MAGIC_ACCESSOR, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, MAGIC_ACCESSOR);
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
		return define(cw, callerLoader);
	}

	private static <T> ReflectInvoker<T> generic(ClassLoader callerLoader, Class<?> clazz, String fieldName, Class<T> type, boolean isStatic, boolean isFinal, boolean deepReflect) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		String desc = getDescriptor(type);
		String owner = clazz.getTypeName().replace('.', '/');
		final OperandStack stack = new OperandStack();
		ClassWriter cw = new ClassWriter(0);
		cw.visit(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<"+getDescriptor(type)+">;", MAGIC_ACCESSOR, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, MAGIC_ACCESSOR);
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
		return define(cw, callerLoader);
	}

	private static <T> ReflectInvoker<T> generic(ClassLoader callerLoader, Class<T> clazz, MethodType type) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		String desc = type.toMethodDescriptorString();
		final OperandStack stack = new OperandStack();
		String owner = clazz.getTypeName().replace('.', '/');
		ClassWriter cw = new ClassWriter(0);
		cw.visit(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<"+getDescriptor(clazz)+">;", MAGIC_ACCESSOR, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, MAGIC_ACCESSOR);
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
		return define(cw, callerLoader);
	}

	private static <T> ReflectInvoker<T> generic(ClassLoader callerLoader, Class<T> clazz) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, AccessFlag.ACC_STRICT | AccessFlag.ACC_PUBLIC, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<"+getDescriptor(clazz)+">;", MAGIC_ACCESSOR, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, MAGIC_ACCESSOR);
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)"+getDescriptor(clazz), null, null);
		mv.visitTypeInsn(Opcodes.NEW, clazz.getTypeName().replace('.', '/'));
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(1, 2);
		mv.visitEnd();
		bridge(cw, className, clazz);
		return define(cw, callerLoader);
	}

	private static ReflectionClassLoader getClassLoader(ClassLoader callerLoader)
	{
		if (callerLoader == ClassLoader.getSystemClassLoader()) return INTERNAL_CLASS_LOADER;
		return CLASS_LOADER_MAP.computeIfAbsent(callerLoader, CLASS_LOADER_FACTORY::invoke);
	}

	private static void bridge(ClassWriter cw, String className, Class<?> returnType)
	{
		if (returnType == Object.class) return;
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
			else mv.visitTypeInsn(Opcodes.CHECKCAST, c.isArray() ? getDescriptor(c) : getType(c));
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
		if (c == boolean.class) mv.visitTypeInsn(Opcodes.CHECKCAST, getType(Boolean.class));
		else if (c == char.class) mv.visitTypeInsn(Opcodes.CHECKCAST, getType(Character.class));
		else mv.visitTypeInsn(Opcodes.CHECKCAST, getType(Number.class));

		if (c == byte.class)mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "byteValue", "()B", false);
		else if (c == short.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "shortValue", "()S", false);
		else if (c == int.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
		else if (c == long.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J", false);
		else if (c == float.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", false);
		else if (c == double.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
		else if (c == boolean.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
		else if (c == char.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
	}

	private static <T> ReflectInvoker<T> define(ClassWriter cw, ClassLoader callerLoader) throws Throwable
	{
		cw.visitEnd();
		byte[] code = cw.toByteArray();
//		Class<?> implClass = (Class<?>) DEFINE.invoke(loader, null, code, 0, code.length);
		Class<?> implClass = getClassLoader(callerLoader).define(code);
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

			/*
			 * disable reflect warn
			 */
			{
				Field field = Unsafe.class.getDeclaredField("theUnsafe");
				field.setAccessible(true);
				UNSAFE = (Unsafe) field.get(null);

				if (majorVersion > 0X34)
				{
					String name = "jdk.internal.module.IllegalAccessLogger";
					Class<?> clazz = Class.forName(name);
					Field loggerField = clazz.getDeclaredField("logger");
					long offset = UNSAFE.staticFieldOffset(loggerField);
					UNSAFE.putObjectVolatile(clazz, offset, null);
				}

				if (majorVersion <= 0X34) MAGIC_ACCESSOR = "sun/reflect/MagicAccessorImpl";
				else MAGIC_ACCESSOR = "jdk/internal/reflect/MagicAccessorImpl";
			}

			{
				DELEGATING_CLASS = Class.forName(majorVersion > 0x34 ? "jdk.internal.reflect.DelegatingClassLoader" : "sun.reflect.DelegatingClassLoader");
				Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
				field.setAccessible(true);
				MethodHandles.Lookup lookup = TRUSTED_LOOKUP = (MethodHandles.Lookup) field.get(null);
				DEFINE = lookup.findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class));
//				INTERNAL_CLASS_LOADER = new ReflectionClassLoader(ReflectInvoker.class.getClassLoader());
			}

			/*
			 * check jdk.internal accessible
			 */
			{
				MethodHandle jla_handle = TRUSTED_LOOKUP.findStaticGetter(
					Class.forName(new String("jdk.internal.misc.SharedSecrets".getBytes())),
					"javaLangAccess",
					Class.forName(new String("jdk.internal.misc.JavaLangAccess".getBytes()))
				);
				Object jla = jla_handle.invoke();
				MethodHandle exports = TRUSTED_LOOKUP.findVirtual(
					Class.forName(new String("jdk.internal.misc.JavaLangAccess".getBytes())),
					"addExportsToAllUnnamed",
					MethodType.methodType(
						void.class,
						Class.forName("java.lang.Module"),
						String.class
					)
				);
				System.out.println(Class.class.getMethod("getModule").invoke(Object.class));
				exports.invoke(
					jla,
					Class.class.getMethod("getModule").invoke(Object.class),
					"jdk.internal.loader"
				);
			}

			/*
			 * Method Handle Invoker
			 */
			{
				Class<?> handleInvoker;
				try
				{
					handleInvoker = Class.forName("org.mve.util.reflect.MethodHandleInvoker");
				}
				catch (Throwable t)
				{
					ClassWriter cw = new ClassWriter(0);
					cw.visit(
						52,
						AccessFlag.ACC_SUPER | AccessFlag.ACC_PUBLIC,
						"org/mve/util/reflect/MethodHandleInvoker",
						"Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<Ljava/lang/Class<*>;>;",
						"java/lang/Object",
						new String[]{"org/mve/util/reflect/ReflectInvoker"}
					);
					genericConstructor(cw, "java/lang/Object");
					MethodVisitor mv = cw.visitMethod(
						AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS,
						"invoke",
						"([Ljava/lang/Object;)Ljava/lang/Object;",
						null,
						null
					);
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
					mv.visitMethodInsn(
						Opcodes.INVOKEVIRTUAL,
						getType(MethodHandle.class),
						"invokeWithArguments",
						MethodType.methodType(
							Object.class,
							Object[].class
						).toMethodDescriptorString(),
						false
					);
					mv.visitInsn(Opcodes.ARETURN);
					mv.visitMaxs(4, 2);
					mv.visitEnd();
					cw.visitEnd();
					byte[] code = cw.toByteArray();
					handleInvoker = (Class<?>) DEFINE.invoke(ReflectInvoker.class.getClassLoader(), null, code, 0, code.length);
				}
//				if (handleInvoker == null) throw new UnknownError();
				METHOD_HANDLE_INVOKER = (ReflectInvoker<Object>) handleInvoker.getDeclaredConstructor().newInstance();

			}

			/*
			 * ClassLoader class
			 */
			Class<?> internalClassLoader;
			{
				String internal_classloader_name = majorVersion > 0x34 ? "jdk/internal/loader/ClassLoader" : "org/mve/util/reflect/ClassLoader";
				String superClass = majorVersion > 0x34 ? "jdk/internal/loader/BuiltinClassLoader" : "java/lang/ClassLoader";
				try
				{
					internalClassLoader = Class.forName(internal_classloader_name.replace('/', '.'));
				}
				catch (Throwable t)
				{
					ClassWriter cw = new ClassWriter(0);
					cw.visit(
						0x34,
						AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER,
						internal_classloader_name,
						null,
						superClass,
						new String[]{getType(ReflectionClassLoader.class)}
					);
					/*
					 * fields
					 */
					{
						/*
						 * private final Map<String, Class<?>> class
						 */
						FieldVisitor fv = cw.visitField(
							AccessFlag.ACC_PRIVATE | AccessFlag.ACC_SYNTHETIC | AccessFlag.ACC_FINAL,
							"class",
							getDescriptor(Map.class),
							"Ljava/util/Map<Ljava/lang/String;Ljava/lang/Class<*>;>;",
							null
						);
						fv.visitEnd();

						/*
						 * private final ClassLoader this
						 */
						fv = cw.visitField(
							AccessFlag.ACC_FINAL | AccessFlag.ACC_SYNTHETIC | AccessFlag.ACC_PRIVATE,
							"this",
							getDescriptor(ClassLoader.class),
							null,
							null
						);
						fv.visitEnd();
					}

					/*
					 * Constructor
					 *   (ClassLoader)
					 */
					{
						MethodVisitor mv = cw.visitMethod(
							AccessFlag.ACC_PUBLIC,
							"<init>",
							MethodType.methodType(
								void.class,
								ClassLoader.class
							).toMethodDescriptorString(),
							null,
							null
						);
						mv.visitCode();
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						if (majorVersion > 0x34) mv.visitLdcInsn(UUID.randomUUID().toString());
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(ClassLoader.class),
							"getParent",
							MethodType.methodType(
								ClassLoader.class
							).toMethodDescriptorString(),
							false
						);
						if (majorVersion > 0x34) mv.visitTypeInsn(Opcodes.CHECKCAST, superClass);
						if (majorVersion > 0x34) mv.visitInsn(Opcodes.ACONST_NULL);
						mv.visitMethodInsn(
							Opcodes.INVOKESPECIAL,
							superClass,
							"<init>",
							(
								majorVersion > 0x34 ?
									MethodType.methodType(
										void.class,
										String.class,
										Class.forName(superClass.replace('/', '.')),
										Class.forName(new String("jdk.internal.loader.URLClassPath".getBytes()))
									)
									:
									MethodType.methodType(
										void.class,
										ClassLoader.class
									)
							).toMethodDescriptorString(),
							false
						);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitTypeInsn(Opcodes.NEW, getType(ConcurrentHashMap.class));
						mv.visitInsn(Opcodes.DUP);
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(ConcurrentHashMap.class),
							"<init>",
							"()V",
							false
						);
						mv.visitFieldInsn(
							Opcodes.PUTFIELD,
							internal_classloader_name,
							"class",
							getDescriptor(Map.class)
						);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(ClassLoader.class),
							"getParent",
							MethodType.methodType(ClassLoader.class).toMethodDescriptorString(),
							false
						);
						mv.visitVarInsn(Opcodes.ASTORE, 2);
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(ReflectInvokeFactory.class),
							"TRUSTED_LOOKUP",
							getDescriptor(MethodHandles.Lookup.class)
						);
						mv.visitLdcInsn(Type.getType(ClassLoader.class));
						mv.visitLdcInsn("parent");
						mv.visitLdcInsn(Type.getType(ClassLoader.class));
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(MethodHandles.Lookup.class),
							"findSetter",
							MethodType.methodType(
								MethodHandle.class,
								Class.class,
								String.class,
								Class.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitInsn(Opcodes.ICONST_2);
						mv.visitTypeInsn(Opcodes.ANEWARRAY, getType(Object.class));
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitVarInsn(Opcodes.ALOAD, 2);
						mv.visitInsn(Opcodes.AASTORE);
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ICONST_1);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitInsn(Opcodes.AASTORE);
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(MethodHandle.class),
							"invoke",
							MethodType.methodType(
								Object.class,
								Object[].class
							).toMethodDescriptorString(),
							false
						);
						mv.visitInsn(Opcodes.POP);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(ReflectInvokeFactory.class),
							"TRUSTED_LOOKUP",
							getDescriptor(MethodHandles.Lookup.class)
						);
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(ReflectInvokeFactory.class),
							"DELEGATING_CLASS",
							getDescriptor(Class.class)
						);
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(Void.class),
							"TYPE",
							getDescriptor(Class.class)
						);
						mv.visitLdcInsn(Type.getType(ClassLoader.class));
						mv.visitMethodInsn(
							Opcodes.INVOKESPECIAL,
							getType(MethodType.class),
							"methodType",
							MethodType.methodType(
								MethodType.class,
								Class.class,
								Class.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(MethodHandles.Lookup.class),
							"findConstructor",
							MethodType.methodType(
								MethodHandle.class,
								Class.class,
								MethodType.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitInsn(Opcodes.ICONST_1);
						mv.visitTypeInsn(Opcodes.ANEWARRAY, getType(Object.class));
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitInsn(Opcodes.AASTORE);
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(MethodHandle.class),
							"invoke",
							MethodType.methodType(
								Object.class,
								Object[].class
							).toMethodDescriptorString(),
							false
						);
						mv.visitTypeInsn(Opcodes.CHECKCAST, getType(ClassLoader.class));
						mv.visitFieldInsn(
							Opcodes.PUTFIELD,
							internal_classloader_name,
							"this",
							getDescriptor(ClassLoader.class)
						);
						mv.visitInsn(Opcodes.RETURN);
						mv.visitMaxs(6, 3);
						mv.visitEnd();
					}

					/*
					 * implement method define
					 *   Class<?> define(byte[])
					 */
					{
						MethodVisitor mv = cw.visitMethod(
							AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL | AccessFlag.ACC_SYNTHETIC,
							"define",
							MethodType.methodType(
								Class.class,
								byte[].class
							).toMethodDescriptorString(),
							"("
								.concat(getDescriptor(byte[].class))
								.concat(")L")
								.concat(getType(Class.class))
								.concat("<*>;"),
							null
						);
						mv.visitCode();
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(ReflectInvokeFactory.class),
							"DEFINE",
							getDescriptor(MethodHandle.class)
						);
						mv.visitInsn(Opcodes.ICONST_5);
						mv.visitTypeInsn(Opcodes.ANEWARRAY, getType(Object.class));
						// class loader
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitFieldInsn(
							Opcodes.GETFIELD,
							internal_classloader_name,
							"this",
							getDescriptor(ClassLoader.class)
						);
						mv.visitInsn(Opcodes.AASTORE);
						// null
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ICONST_1);
						mv.visitInsn(Opcodes.ACONST_NULL);
						mv.visitInsn(Opcodes.AASTORE);
						// code
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ICONST_2);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitInsn(Opcodes.AASTORE);
						// 0
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ICONST_3);
						mv.visitInsn(Opcodes.ICONST_0);
						warp(int.class, mv);
						mv.visitInsn(Opcodes.AASTORE);
						// code.length
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ICONST_4);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitTypeInsn(Opcodes.CHECKCAST, "[B");
						mv.visitInsn(Opcodes.ARRAYLENGTH);
						warp(int.class, mv);
						mv.visitInsn(Opcodes.AASTORE);
						// invoke method
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(MethodHandle.class),
							"invoke",
							MethodType.methodType(
								Object.class,
								Object[].class
							).toMethodDescriptorString(),
							false
						);
						mv.visitTypeInsn(Opcodes.CHECKCAST, getType(Class.class));
						mv.visitVarInsn(Opcodes.ASTORE, 2);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitFieldInsn(
							Opcodes.GETFIELD,
							internal_classloader_name,
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
					}

					/*
					 * loadClassOrNull / findClass
					 * 		9+				8
					 *
					 * 9+	Class<?> loadClassOrNull(String,boolean)
					 * 8	Class<?> findClass(String)
					 */
					{
						boolean flag = (majorVersion > 0x34);
						String methodName = flag ? "loadClassOrNull" : "findClass";
						String desc = flag ? "(Ljava/lang/String;Z)Ljava/lang/Class;" : "(Ljava/lang/String;)Ljava/lang/Class;";
						String signature = flag ? "(Ljava/lang/String;Z)Ljava/lang/Class<*>;" : "(Ljava/lang/String;)Ljava/lang/Class<*>;";
						MethodVisitor mv = cw.visitMethod(
							AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL,
							methodName,
							desc,
							signature,
							null
						);
						mv.visitCode();
						Label ret = new Label();
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(ClassLoader.class),
							"findLoadedClass",
							MethodType.methodType(
								Class.class,
								String.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitInsn(Opcodes.DUP);
						mv.visitJumpInsn(Opcodes.IFNONNULL, ret);
						mv.visitInsn(Opcodes.POP);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitFieldInsn(
							Opcodes.GETFIELD,
							internal_classloader_name,
							"class",
							getDescriptor(Map.class)
						);
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
						mv.visitInsn(Opcodes.DUP);
						mv.visitJumpInsn(Opcodes.IFNONNULL, ret);
						mv.visitFrame(			// FRAME
							Opcodes.F_SAME1,
							0,
							null,
							1,
							new Object[]{"java/lang/Class"}
						);
						mv.visitInsn(Opcodes.POP);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(ClassLoader.class),
							"getParent",
							MethodType.methodType(
								ClassLoader.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(ClassLoader.class),
							"loadClass",
							MethodType.methodType(
								Class.class,
								String.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitFrame(			// FRAME
							Opcodes.F_SAME1,
							0,
							null,
							1,
							new Object[]{"java/lang/Class"}
						);
						mv.visitLabel(ret);
						mv.visitInsn(Opcodes.ARETURN);
						mv.visitMaxs(2, flag ? 3 : 2);
					}

					byte[] code = cw.toByteArray();
					internalClassLoader = (Class<?>) METHOD_HANDLE_INVOKER.invoke(DEFINE, ClassLoader.getSystemClassLoader(), null, code, 0, code.length);
				}
			}

			/*
			 * class loader constructor
			 */
			{
				Class<?> c;
				try
				{
					c = Class.forName("org/mve/util/reflect/ClassLoaderConstructor");
				}
				catch (Throwable t)
				{
					ClassWriter cw = new ClassWriter(0);
					genericConstructor(cw, "java/lang/Object");
					cw.visit(
						0x34,
						0x21,
						"org/mve/util/reflect/ClassLoaderConstructor",
						"Ljava/lang/Object;Lorg/mve/util/reflect/ReflectInvoker<Lorg/mve/util/reflect/ReflectionClassLoader;>;",
						"java/lang/Object",
						new String[]{"org/mve/util/reflect/ReflectInvoker"}
					);
					MethodVisitor mv = cw.visitMethod(
						0x01,
						"invoke",
						MethodType.methodType(
							ReflectionClassLoader.class,
							Object[].class
						).toMethodDescriptorString(),
						null,
						null
					);
					mv.visitCode();
					if (majorVersion > 0x34)
					{
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitInsn(Opcodes.AALOAD);
						mv.visitTypeInsn(Opcodes.INSTANCEOF, "jdk/internal/loader/BuiltinClassLoader");
						Label l1 = new Label();
						mv.visitJumpInsn(Opcodes.IFEQ, l1);
						mv.visitTypeInsn(Opcodes.NEW, getType(internalClassLoader));
						mv.visitInsn(Opcodes.DUP);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitInsn(Opcodes.AALOAD);
						mv.visitTypeInsn(Opcodes.CHECKCAST, getType(ClassLoader.class));
						mv.visitMethodInsn(
							Opcodes.INVOKESPECIAL,
							getType(internalClassLoader),
							"<init>",
							MethodType.methodType(
								void.class,
								ClassLoader.class
							).toMethodDescriptorString(),
							false
						);
						Label l2 = new Label();
						mv.visitJumpInsn(Opcodes.GOTO, l2);
						mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
						mv.visitLabel(l1);
						mv.visitTypeInsn(Opcodes.NEW, getType(StandardReflectionClassLoader.class));
						mv.visitInsn(Opcodes.DUP);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitInsn(Opcodes.AALOAD);
						mv.visitTypeInsn(Opcodes.CHECKCAST, getType(ClassLoader.class));
						mv.visitMethodInsn(
							Opcodes.INVOKESPECIAL,
							getType(StandardReflectionClassLoader.class),
							"<init>",
							MethodType.methodType(
								void.class,
								ClassLoader.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitLabel(l2);
						mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/ClassLoader"});
					}
					else
					{
						mv.visitTypeInsn(Opcodes.NEW, getType(StandardReflectionClassLoader.class));
						mv.visitInsn(Opcodes.DUP);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitInsn(Opcodes.AALOAD);
						mv.visitTypeInsn(Opcodes.CHECKCAST, getType(ClassLoader.class));
						mv.visitMethodInsn(
							Opcodes.INVOKESPECIAL,
							getType(StandardReflectionClassLoader.class),
							"<init>",
							MethodType.methodType(
								void.class,
								ClassLoader.class
							).toMethodDescriptorString(),
							false
						);
					}
					mv.visitInsn(Opcodes.ARETURN);
					mv.visitMaxs(4, 2);
					mv.visitEnd();
					bridge(cw, "org/mve/util/reflect/ClassLoaderConstructor", ReflectionClassLoader.class);
					cw.visitEnd();
					byte[] code = cw.toByteArray();
					FileOutputStream out = new FileOutputStream("ClassLoaderConstructor.class");
					out.write(code);
					out.flush();
					out.close();
					c = (Class<?>) METHOD_HANDLE_INVOKER.invoke(DEFINE, ClassLoader.getSystemClassLoader(), null, code, 0, code.length);
				}

				CLASS_LOADER_FACTORY = (ReflectInvoker<ReflectionClassLoader>) c.getDeclaredConstructor().newInstance();
			}

			INTERNAL_CLASS_LOADER = CLASS_LOADER_FACTORY.invoke(ClassLoader.getSystemClassLoader());

			/*
			 * caller class
			 */
			{
			}
		}
		catch (Throwable t)
		{
			throw new UninitializedException(t);
		}
	}
}
