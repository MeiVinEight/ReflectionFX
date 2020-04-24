package org.mve.util.reflect;

import org.jetbrains.org.objectweb.asm.ClassWriter;
import org.jetbrains.org.objectweb.asm.FieldVisitor;
import org.jetbrains.org.objectweb.asm.Label;
import org.jetbrains.org.objectweb.asm.MethodVisitor;
import org.jetbrains.org.objectweb.asm.Opcodes;
import org.jetbrains.org.objectweb.asm.Type;
import org.mve.util.asm.OperandStack;
import org.mve.util.asm.file.AccessFlag;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings({"unchecked"})
public class ReflectionFactory
{
	public static final Unsafe UNSAFE;
	public static final MethodHandles.Lookup TRUSTED_LOOKUP;
	public static final MethodHandle DEFINE;
	public static final ReflectionAccessor<Object> METHOD_HANDLE_INVOKER;
	public static final StackAccessor STACK_ACCESSOR;
	public static final Accessor ACCESSOR;
	public static final Class<?> DELEGATING_CLASS;
	private static final ReflectionClassLoader INTERNAL_CLASS_LOADER;
	private static final String MAGIC_ACCESSOR;
	private static final ReflectionAccessor<ReflectionClassLoader> CLASS_LOADER_FACTORY;
	private static final Map<ClassLoader, ReflectionClassLoader> CLASS_LOADER_MAP = new ConcurrentHashMap<>();
	private static int id = 0;

	public static <T> ReflectionAccessor<T> getReflectionAccessor(Class<?> clazz, String methodName, boolean isStatic, Class<T> returnType, Class<?>... params)
	{
		try { return generic(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (STACK_ACCESSOR.getCallerClass()::getClassLoader)), clazz, methodName, MethodType.methodType(returnType, params), isStatic); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectionAccessor<T> getReflectionAccessor(Class<?> clazz, String fieldName, Class<T> type, boolean isStatic, boolean isFinal)
	{
		try { return generic(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (STACK_ACCESSOR.getCallerClass()::getClassLoader)), clazz, fieldName, type, isStatic, isFinal, false); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectionAccessor<T> getReflectionAccessor(Class<?> clazz, String fieldName, Class<T> type, boolean isStatic, boolean isFinal, boolean deepReflect)
	{
		try { return generic(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (STACK_ACCESSOR.getCallerClass()::getClassLoader)), clazz, fieldName, type, isStatic, isFinal, deepReflect); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectionAccessor<T> getReflectionAccessor(Class<T> clazz)
	{
		try { return generic(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (STACK_ACCESSOR.getCallerClass()::getClassLoader)), clazz); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectionAccessor<T> getReflectionAccessor(Class<T> clazz, boolean initialize)
	{
		try { return initialize ? generic(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (STACK_ACCESSOR.getCallerClass()::getClassLoader)), clazz, MethodType.methodType(void.class)) : generic(STACK_ACCESSOR.getCallerClass().getClassLoader(), clazz); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectionAccessor<T> getReflectionAccessor(Class<T> clazz, Class<?>... params)
	{
		try { return generic(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (STACK_ACCESSOR.getCallerClass()::getClassLoader)), clazz, MethodType.methodType(void.class, params)); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectionAccessor<Void> throwException()
	{
		String className = "org/mve/util/reflect/ReflectionAccessorImpl"+id++;
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectionAccessor<Ljava/lang/Void;>;", "java/lang/Object", new String[]{"org/mve/util/reflect/ReflectionAccessor"});
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
		try { return define(cw, AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (STACK_ACCESSOR.getCallerClass()::getClassLoader))); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static <T> ReflectionAccessor<T> constant(T value)
	{
		String className = "org/mve/util/reflect/ReflectionAccessorImpl"+id++;
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectionAccessor<"+getDescriptor(value.getClass())+">;", "java/lang/Object", new String[]{"org/mve/util/reflect/ReflectionAccessor"});
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
			Class<?> clazz = getClassLoader(AccessController.doPrivileged((PrivilegedAction<ClassLoader>) (STACK_ACCESSOR.getCallerClass()::getClassLoader))).define(code);
			return (ReflectionAccessor<T>) clazz.getDeclaredConstructor(value.getClass()).newInstance(value);
		}
		catch (Throwable throwable)
		{
			throw new ReflectionGenericException("Can not generic invoker", throwable);
		}
	}

	private static <T> ReflectionAccessor<T> generic(ClassLoader callerLoader, Class<?> clazz, String methodName, MethodType type, boolean isStatic) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectionAccessorImpl"+id++;
		String desc = type.toMethodDescriptorString();
		final String owner = clazz.getTypeName().replace('.', '/');
		Class<?> returnType = type.returnType();
		Class<?>[] params = type.parameterArray();
		final OperandStack stack = new OperandStack();
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectionAccessor<"+getDescriptor(typeWarp(returnType))+">;", MAGIC_ACCESSOR, new String[]{"org/mve/util/reflect/ReflectionAccessor"});
		genericConstructor(cw, MAGIC_ACCESSOR);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)"+getDescriptor(typeWarp(returnType)), null, null);
		mv.visitCode();
		if (!isStatic) arrayFirst(mv, owner, stack);
		pushArguments(params, mv, isStatic ? 0 : 1, stack);
		if (isStatic) mv.visitMethodInsn(Opcodes.INVOKESTATIC, owner, methodName, desc, false);
		else mv.visitMethodInsn(methodName.equals("<init>") ? Opcodes.INVOKESPECIAL : Opcodes.INVOKEVIRTUAL, owner, methodName, desc, false);
		for (int i = 0; i < params.length; i++) stack.pop();
		if (!isStatic) stack.pop();
		if (returnType == void.class) { mv.visitInsn(Opcodes.ACONST_NULL); stack.push(); }
		else
		{
			warp(returnType, mv);
			if (returnType == long.class || returnType == double.class) stack.pop();
		}
		mv.visitInsn(Opcodes.ARETURN);
		stack.pop();
		mv.visitMaxs(stack.getMaxSize(), 2);
		mv.visitEnd();
		bridge(cw, className, typeWarp(returnType));
		return define(cw, callerLoader);
	}

	private static <T> ReflectionAccessor<T> generic(ClassLoader callerLoader, Class<?> clazz, String fieldName, Class<T> type, boolean isStatic, boolean isFinal, boolean deepReflect) throws Throwable
	{
		if (typeWarp(type) == Void.class) throw new IllegalArgumentException("illegal type: void");
		String className = "org/mve/util/reflect/ReflectionAccessorImpl"+id++;
		String desc = getDescriptor(type);
		String owner = clazz.getTypeName().replace('.', '/');
		final OperandStack stack = new OperandStack();
		ClassWriter cw = new ClassWriter(0);
		cw.visit(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectionAccessor<"+getDescriptor(typeWarp(type))+">;", MAGIC_ACCESSOR, new String[]{"org/mve/util/reflect/ReflectionAccessor"});
		genericConstructor(cw, MAGIC_ACCESSOR);
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)"+getDescriptor(typeWarp(type)), null, null);
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
					mv.visitFieldInsn(Opcodes.GETSTATIC, getType(ReflectionFactory.class), "UNSAFE", getDescriptor(Unsafe.class));
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
						if (type == long.class || type == double.class) stack.push();
						if (type == byte.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getDescriptor(Unsafe.class), "putByteVolatile", "(Ljava/lang/Object;JB)V", false);
						else if (type == short.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getDescriptor(Unsafe.class), "putShortVolatile", "(Ljava/lang/Object;JS)V", false);
						else if (type == int.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getDescriptor(Unsafe.class), "putIntVolatile", "(Ljava/lang/Object;JI)V", false);
						else if (type == long.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getDescriptor(Unsafe.class), "putLongVolatile", "(Ljava/lang/Object;JJ)V", false);
						else if (type == float.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getDescriptor(Unsafe.class), "putFloatVolatile", "(Ljava/lang/Object;JF)V", false);
						else if (type == double.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getDescriptor(Unsafe.class), "putDoubleVolatile", "(Ljava/lang/Object;JD)V", false);
						else if (type == boolean.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getDescriptor(Unsafe.class), "putBooleanVolatile", "(Ljava/lang/Object;JZ)V", false);
						else if (type == char.class) mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getDescriptor(Unsafe.class), "putCharVolatile", "(Ljava/lang/Object;JC)V", false);
					}
					else mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, getDescriptor(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", false);
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
			if (type.isPrimitive())
			{
				unwarp(type, mv);
				if (type == long.class || type == double.class) stack.push();
			}
			else mv.visitTypeInsn(Opcodes.CHECKCAST, type.getTypeName().replace('.', '/'));
			mv.visitFieldInsn(isStatic ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD, owner, fieldName, desc);
			stack.pop();
			if (!isStatic) stack.pop();
		}
		mv.visitLabel(label);
		if (!isStatic) arrayFirst(mv, owner, stack);
		mv.visitFieldInsn(isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD, owner, fieldName, desc);
		if (isStatic) stack.push();
		if (type.isPrimitive())
		{
			warp(type, mv);
			if (type == long.class || type == double.class) stack.pop();
		}
		mv.visitInsn(Opcodes.ARETURN);
		stack.pop();
		mv.visitMaxs(stack.getMaxSize(), 2);
		mv.visitEnd();
		bridge(cw, className, typeWarp(type));
		return define(cw, callerLoader);
	}

	private static <T> ReflectionAccessor<T> generic(ClassLoader callerLoader, Class<T> clazz, MethodType type) throws Throwable
	{
		if (clazz == void.class || clazz.isPrimitive() || clazz.isArray()) throw new IllegalArgumentException("illegal type: "+clazz);
		String className = "org/mve/util/reflect/ReflectionAccessorImpl"+id++;
		String desc = type.toMethodDescriptorString();
		final OperandStack stack = new OperandStack();
		String owner = clazz.getTypeName().replace('.', '/');
		ClassWriter cw = new ClassWriter(0);
		cw.visit(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectionAccessor<"+getDescriptor(clazz)+">;", MAGIC_ACCESSOR, new String[]{"org/mve/util/reflect/ReflectionAccessor"});
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

	private static <T> ReflectionAccessor<T> generic(ClassLoader callerLoader, Class<T> clazz) throws Throwable
	{
		if (typeWarp(clazz) == Void.class || clazz.isPrimitive() || clazz.isArray()) throw new IllegalArgumentException("illegal type: "+clazz);
		String className = "org/mve/util/reflect/ReflectionAccessorImpl"+id++;
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, AccessFlag.ACC_STRICT | AccessFlag.ACC_PUBLIC, className, "Ljava/lang/Object;Lorg/mve/util/reflect/ReflectionAccessor<"+getDescriptor(clazz)+">;", MAGIC_ACCESSOR, new String[]{"org/mve/util/reflect/ReflectionAccessor"});
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
		if (callerLoader == ReflectionFactory.class.getClassLoader()) return INTERNAL_CLASS_LOADER;
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
			if (c.isPrimitive())
			{
				unwarp(c, mv);
				if (c == long.class || c == double.class) stack.push();
			}
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

	private static Class<?> typeWarp(Class<?> type)
	{
		if (type == void.class) return Void.class;
		else if (type == byte.class) return Byte.class;
		else if (type == short.class) return Short.class;
		else if (type == int.class) return Integer.class;
		else if (type == long.class) return Long.class;
		else if (type == float.class) return Float.class;
		else if (type == double.class) return Double.class;
		else if (type == boolean.class) return Boolean.class;
		else if (type == char.class) return Character.class;
		else return type;
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

	private static <T> ReflectionAccessor<T> define(ClassWriter cw, ClassLoader callerLoader) throws Throwable
	{
		cw.visitEnd();
		byte[] code = cw.toByteArray();
		Class<?> implClass = getClassLoader(callerLoader).define(code);
		return (ReflectionAccessor<T>) implClass.getDeclaredConstructor().newInstance();
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
			int majorVersion = new DataInputStream(in).readShort() & 0XFFFF;
			in.close();

			DELEGATING_CLASS = Class.forName(majorVersion > 0x34 ? "jdk.internal.reflect.DelegatingClassLoader" : "sun.reflect.DelegatingClassLoader");

			/*
			 * Unsafe
			 */
			sun.misc.Unsafe usf;
			{
				Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
				field.setAccessible(true);
				usf = (sun.misc.Unsafe) field.get(null);
			}

			/*
			 * trusted lookup
			 */
			{
				Object illegalAccessLogger = null;
				if (majorVersion > 0X34)
				{
					String name = "jdk.internal.module.IllegalAccessLogger";
					Class<?> clazz = Class.forName(name);
					Field loggerField = clazz.getDeclaredField("logger");
					long offset = usf.staticFieldOffset(loggerField);
					illegalAccessLogger = usf.getObjectVolatile(clazz, offset);
					usf.putObjectVolatile(clazz, offset, null);
				}

				{
					Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
					field.setAccessible(true);
					MethodHandles.Lookup lookup = TRUSTED_LOOKUP = (MethodHandles.Lookup) field.get(null);
					DEFINE = lookup.findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class));
				}

				if (majorVersion > 0X34)
				{
					String name = "jdk.internal.module.IllegalAccessLogger";
					Class<?> clazz = Class.forName(name);
					Field loggerField = clazz.getDeclaredField("logger");
					long offset = usf.staticFieldOffset(loggerField);
					usf.putObjectVolatile(clazz, offset, illegalAccessLogger);
				}

				if (majorVersion <= 0X34) MAGIC_ACCESSOR = "sun/reflect/MagicAccessorImpl";
				else MAGIC_ACCESSOR = "jdk/internal/reflect/MagicAccessorImpl";
			}

			/*
			 * check jdk.internal accessible
			 */
			CHECK:
			{
				String packageName;
				if (majorVersion == 0x34) break CHECK;
				else if (majorVersion >= 0x35 && majorVersion <= 0x37) packageName = "jdk.internal.misc";
				else packageName = "jdk.internal.access";
				Class<?> sharedSecretsClass = Class.forName(packageName + ".SharedSecrets");
				Class<?> javaLangAccessClass = Class.forName(packageName + ".JavaLangAccess");
				MethodHandle jlaHandle = TRUSTED_LOOKUP.findStaticGetter(sharedSecretsClass, "javaLangAccess", javaLangAccessClass);
				Object jla = jlaHandle.invoke();
				MethodHandle handle = TRUSTED_LOOKUP.findVirtual(
					javaLangAccessClass,
					"addExportsToAllUnnamed",
					MethodType.methodType(
						void.class,
						Class.forName("java.lang.Module"),
						String.class
					)
				);
				handle.invoke(jla, Class.class.getMethod("getModule").invoke(Object.class), "jdk.internal.loader");
				handle.invoke(jla, Class.class.getMethod("getModule").invoke(Object.class), "jdk.internal.misc");
			}

			/*
			 * Unsafe wrapper
			 */
			{
				Class<?> usfClass = Class.forName(majorVersion > 0x34 ? "jdk.internal.misc.Unsafe" : "sun.misc.Unsafe");
				String className = "org/mve/util/reflect/UnsafeWrapper";
				Class<?> clazz;
				try
				{
					clazz = Class.forName(className.replace('/', '.'));
				}
				catch (Throwable t)
				{
					ClassWriter cw = new ClassWriter(0);
					cw.visit(
						0x34,
						0x21,
						className,
						null,
						"java/lang/Object",
						new String[]{getType(Unsafe.class)}
					);

					FieldVisitor fv = cw.visitField(
						AccessFlag.ACC_PRIVATE | AccessFlag.ACC_FINAL | AccessFlag.ACC_STATIC,
						"final",
						getDescriptor(usfClass),
						null,
						null
					);
					fv.visitEnd();

					// implement methods
					{
						BiConsumer<String[], Class<?>[]> method = (name, arr) ->
						{
							String desc = MethodType.methodType(arr[0], Arrays.copyOfRange(arr, 1, arr.length)).toMethodDescriptorString();
							MethodVisitor mv = cw.visitMethod(
								AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL,
								name[0],
								desc,
								name.length < 3 ? null : name[2],
								null
							);
							mv.visitCode();
							mv.visitFieldInsn(
								Opcodes.GETSTATIC,
								className,
								"final",
								getDescriptor(usfClass)
							);
							int size = 0;
							for (int i = 1; i < arr.length; i++)
							{
								size++;
								Class<?> type = arr[i];
								if (type == byte.class || type == short.class || type == int.class || type == boolean.class || type == char.class) mv.visitVarInsn(Opcodes.ILOAD, size);
								else if (type == long.class) mv.visitVarInsn(Opcodes.LLOAD, size);
								else if (type == float.class) mv.visitVarInsn(Opcodes.FLOAD, size);
								else if (type == double.class) mv.visitVarInsn(Opcodes.DLOAD, size);
								else mv.visitVarInsn(Opcodes.ALOAD, size);
								if (type == double.class || type == long.class) size++;
							}
							mv.visitMethodInsn(
								Opcodes.INVOKEVIRTUAL,
								getType(usfClass),
								name[1],
								desc,
								false
							);
							Class<?> c = arr[0];
							if (c == void.class) mv.visitInsn(Opcodes.RETURN);
							else if (c == byte.class || c == short.class || c == int.class || c == char.class || c == boolean.class) mv.visitInsn(Opcodes.IRETURN);
							else if (c == long.class) mv.visitInsn(Opcodes.LRETURN);
							else if (c == float.class) mv.visitInsn(Opcodes.FRETURN);
							else if (c == double.class) mv.visitInsn(Opcodes.DRETURN);
							else mv.visitInsn(Opcodes.ARETURN);
							mv.visitMaxs(size+1, size+1);
							mv.visitEnd();
						};

						BiConsumer<String[], Class<?>[]> unsupported = (name, arr) ->
						{
							String desc = MethodType.methodType(arr[0], Arrays.copyOfRange(arr, 1, arr.length)).toMethodDescriptorString();
							MethodVisitor mv =cw.visitMethod(
								AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL,
								name[0],
								desc,
								name.length < 2 ? null : name[1],
								null
							);
							int size = arr.length;
							for (Class<?> c : arr) if (c == long.class || c == double.class) size++;
							mv.visitTypeInsn(Opcodes.NEW, getType(UnsupportedMethodException.class));
							mv.visitInsn(Opcodes.DUP);
							mv.visitLdcInsn("Method "+name[0]+desc+" is unsupported at JVM version "+majorVersion);
							mv.visitMethodInsn(
								Opcodes.INVOKESPECIAL,
								getType(UnsupportedMethodException.class),
								"<init>",
								MethodType.methodType(
									void.class,
									String.class
								).toMethodDescriptorString(),
								false
							);
							mv.visitInsn(Opcodes.ATHROW);
							mv.visitMaxs(3, size);
							mv.visitEnd();
						};

						BiConsumer<String[], Class<?>[]> v35method = (name, arr) ->
						{
							if (majorVersion < 0x35) unsupported.accept(new String[]{name[0], name.length == 3 ? name[2] : null}, arr);
							else method.accept(name, arr);
						};

						BiConsumer<String[], Class<?>[]> v36method = (name, arr) ->
						{
							if (majorVersion < 0x36) unsupported.accept(new String[]{name[0], name.length == 3 ? name[2] : null}, arr);
							else method.accept(name, arr);
						};

						BiConsumer<String, Class<?>> v35GetAndAddSetAR = (str, type) ->
						{
							Class<?>[] arr = new Class[]{
								type,
								Object.class,
								long.class,
								type
							};
							v35method.accept(new String[]{str + "Acquire", str + "Acquire"}, arr);
							v35method.accept(new String[]{str + "Release", str + "Acquire"}, arr);
						};

						BiConsumer<String, Class<?>> v35GetAndBitwise = (name, type) ->
						{
							Class<?>[] arr = new Class[]{
								type,
								Object.class,
								long.class,
								type
							};
							String p = "getAndBitwise";
							String a = "Acquire";
							String r = "Release";
							v35method.accept(new String[]{p + "Or" + name, p + "Or" + name}, arr);
							v35method.accept(new String[]{p + "And" + name, p + "And" + name}, arr);
							v35method.accept(new String[]{p + "Xor" + name, p + "Xor" + name}, arr);
							v35method.accept(new String[]{p + "Or" + name + a, p + "Or" + name + a}, arr);
							v35method.accept(new String[]{p + "And" + name + a, p + "And" + name + a}, arr);
							v35method.accept(new String[]{p + "Xor" + name + a, p + "Xor" + name + a}, arr);
							v35method.accept(new String[]{p + "Or" + name + r, p + "Or" + name + r}, arr);
							v35method.accept(new String[]{p + "And" + name + r, p + "And" + name + r}, arr);
							v35method.accept(new String[]{p + "Xor" + name + r, p + "Xor" + name + r}, arr);
						};

						// byte getByte(long offset);
						method.accept(
							new String[]{
								"getByte",
								"getByte"
							},
							new Class[]{
								byte.class,
								long.class
							}
						);

						// byte getByte(Object obj, long offset);
						method.accept(
							new String[]{
								"getByte",
								"getByte"
							},
							new Class[]{
								byte.class,
								Object.class,
								long.class
							}
						);

						// byte getByteVolatile(Object obj, long offset);
						method.accept(
							new String[]{
								"getByteVolatile",
								"getByteVolatile"
							},
							new Class[]{
								byte.class,
								Object.class,
								long.class
							}
						);

						// void putByte(long offset, byte b);
						method.accept(
							new String[]{
								"putByte",
								"putByte"
							},
							new Class[]{
								void.class,
								long.class,
								byte.class
							}
						);

						// void putByte(Object obj, long offset, byte b);
						method.accept(
							new String[]{
								"putByte",
								"putByte"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								byte.class
							}
						);

						// void putByteVolatile(Object obj, long offset, byte b);
						method.accept(
							new String[]{
								"putByteVolatile",
								"putByteVolatile"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								byte.class
							}
						);

						// short getShort(long offset);
						method.accept(
							new String[]{
								"getShort",
								"getShort"
							},
							new Class[]{
								short.class,
								long.class
							}
						);

						// short getShort(Object obj, long offset);
						method.accept(
							new String[]{
								"getShort",
								"getShort"
							},
							new Class[]{
								short.class,
								Object.class,
								long.class
							}
						);

						// short getShortVolatile(Object obj, long offset);
						method.accept(
							new String[]{
								"getShortVolatile",
								"getShortVolatile"
							},
							new Class[]{
								short.class,
								Object.class,
								long.class
							}
						);

						// void putShort(long offset, short s);
						method.accept(
							new String[]{
								"putShort",
								"putShort"
							},
							new Class[]{
								void.class,
								long.class,
								short.class
							}
						);

						// void putShort(Object obj, long offset, short s);
						method.accept(
							new String[]{
								"putShort",
								"putShort"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								short.class
							}
						);

						// void putShortVolatile(Object obj, long offset, short s);
						method.accept(
							new String[]{
								"putShortVolatile",
								"putShortVolatile"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								short.class
							}
						);

						// int getInt(long offset);
						method.accept(
							new String[]{
								"getInt",
								"getInt"
							},
							new Class[]{
								int.class,
								long.class
							}
						);

						// int getInt(Object obj, long offset);
						method.accept(
							new String[]{
								"getInt",
								"getInt"
							},
							new Class[]{
								int.class,
								Object.class,
								long.class
							}
						);

						// int getIntVolatile(Object obj, long offset);
						method.accept(
							new String[]{
								"getIntVolatile",
								"getIntVolatile"
							},
							new Class[]{
								int.class,
								Object.class,
								long.class
							}
						);

						// void putInt(long offset, int i);
						method.accept(
							new String[]{
								"putInt",
								"putInt"
							},
							new Class[]{
								void.class,
								long.class,
								int.class
							}
						);

						// void putInt(Object obj, long offset, int i);
						method.accept(
							new String[]{
								"putInt",
								"putInt"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								int.class
							}
						);

						// void putIntVolatile(Object obj, long offset, int i);
						method.accept(
							new String[]{
								"putIntVolatile",
								"putIntVolatile"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								int.class
							}
						);

						// long getLong(long offset);
						method.accept(
							new String[]{
								"getLong",
								"getLong"
							},
							new Class[]{
								long.class,
								long.class
							}
						);

						// long getLong(Object obj, long offset);
						method.accept(
							new String[]{
								"getLong",
								"getLong"
							},
							new Class[]{
								long.class,
								Object.class,
								long.class
							}
						);

						// long getLongVolatile(Object obj, long offset);
						method.accept(
							new String[]{
								"getLongVolatile",
								"getLongVolatile"
							},
							new Class[]{
								long.class,
								Object.class,
								long.class
							}
						);

						// void putLong(long offset, long l);
						method.accept(
							new String[]{
								"putLong",
								"putLong"
							},
							new Class[]{
								void.class,
								long.class,
								long.class
							}
						);

						// void putLong(Object obj, long offset, long l);
						method.accept(
							new String[]{
								"putLong",
								"putLong"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								long.class
							}
						);

						// void putLongVolatile(Object obj, long offset, long l);
						method.accept(
							new String[]{
								"putLongVolatile",
								"putLongVolatile"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								long.class
							}
						);

						// float getFloat(long offset);
						method.accept(
							new String[]{
								"getFloat",
								"getFloat"
							},
							new Class[]{
								float.class,
								long.class
							}
						);

						// float getFloat(Object obj, long offset);
						method.accept(
							new String[]{
								"getFloat",
								"getFloat"
							},
							new Class[]{
								float.class,
								Object.class,
								long.class
							}
						);

						// float getFloatVolatile(Object obj, long offset);
						method.accept(
							new String[]{
								"getFloatVolatile",
								"getFloatVolatile"
							},
							new Class[]{
								float.class,
								Object.class,
								long.class
							}
						);

						// void putFloat(long offset, float f);
						method.accept(
							new String[]{
								"putFloat",
								"putFloat"
							},
							new Class[]{
								void.class,
								long.class,
								float.class
							}
						);

						// void putFloat(Object obj, long offset, float f);
						method.accept(
							new String[]{
								"putFloat",
								"putFloat"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								float.class
							}
						);

						// void putFloatVolatile(Object obj, long offset, float f);
						method.accept(
							new String[]{
								"putFloatVolatile",
								"putFloatVolatile"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								float.class
							}
						);

						// double getDouble(long offset);
						method.accept(
							new String[]{
								"getDouble",
								"getDouble"
							},
							new Class[]{
								double.class,
								long.class
							}
						);

						// double getDouble(Object obj, long offset);
						method.accept(
							new String[]{
								"getDouble",
								"getDouble"
							},
							new Class[]{
								double.class,
								Object.class,
								long.class
							}
						);

						// double getDoubleVolatile(Object obj, long offset);
						method.accept(
							new String[]{
								"getDoubleVolatile",
								"getDoubleVolatile"
							},
							new Class[]{
								double.class,
								Object.class,
								long.class
							}
						);

						// void putDouble(long offset, double d);
						method.accept(
							new String[]{
								"putDouble",
								"putDouble"
							},
							new Class[]{
								void.class,
								long.class,
								double.class
							}
						);

						// void putDouble(Object obj, long offset, double d);
						method.accept(
							new String[]{
								"putDouble",
								"putDouble"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								double.class
							}
						);

						// void putDoubleVolatile(Object obj, long offset, double d);
						method.accept(
							new String[]{
								"putDoubleVolatile",
								"putDoubleVolatile"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								double.class
							}
						);

						// boolean getBoolean(long offset);
						method.accept(
							new String[]{
								"getBoolean",
								"getBoolean"
							},
							new Class[]{
								boolean.class,
								long.class
							}
						);

						// boolean getBoolean(Object obj, long offset);
						method.accept(
							new String[]{
								"getBoolean",
								"getBoolean"
							},
							new Class[]{
								boolean.class,
								Object.class,
								long.class
							}
						);

						// boolean getBooleanVolatile(Object obj, long offset);
						method.accept(
							new String[]{
								"getBooleanVolatile",
								"getBooleanVolatile"
							},
							new Class[]{
								boolean.class,
								Object.class,
								long.class
							}
						);

						// void putBoolean(long offset, boolean b);
						method.accept(
							new String[]{
								"putBoolean",
								"putBoolean"
							},
							new Class[]{
								void.class,
								long.class,
								boolean.class
							}
						);

						// void putBoolean(Object obj, long offset, boolean b);
						method.accept(
							new String[]{
								"putBoolean",
								"putBoolean"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								boolean.class
							}
						);

						// void putBooleanVolatile(Object obj, long offset, boolean b);
						method.accept(
							new String[]{
								"putBooleanVolatile",
								"putBooleanVolatile"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								boolean.class
							}
						);

						// char getChar(long offset);
						method.accept(
							new String[]{
								"getChar",
								"getChar"
							},
							new Class[]{
								char.class,
								long.class
							}
						);

						// char getChar(Object obj, long offset);
						method.accept(
							new String[]{
								"getChar",
								"getChar"
							},
							new Class[]{
								char.class,
								Object.class,
								long.class
							}
						);

						// char getCharVolatile(Object obj, long offset);
						method.accept(
							new String[]{
								"getCharVolatile",
								"getCharVolatile"
							},
							new Class[]{
								char.class,
								Object.class,
								long.class
							}
						);

						// void putChar(long offset, char c);
						method.accept(
							new String[]{
								"putChar",
								"putChar"
							},
							new Class[]{
								void.class,
								long.class,
								char.class
							}
						);

						// void putChar(Object obj, long offset, char c);
						method.accept(
							new String[]{
								"putChar",
								"putChar"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								char.class
							}
						);

						// void putCharVolatile(Object obj, long offset, char c);
						method.accept(
							new String[]{
								"putCharVolatile",
								"putCharVolatile"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								char.class
							}
						);

						// Object getObject(Object obj, long offset);
						method.accept(
							new String[]{
								"getObject",
								majorVersion > 0x37 ? "getReference" : "getObject"
							},
							new Class[]{
								Object.class,
								Object.class,
								long.class
							}
						);

						// Object getReference(Object obj, long offset);
						method.accept(
							new String[]{
								"getReference",
								majorVersion > 0x37 ? "getReference" : "getObject"
							},
							new Class[]{
								Object.class,
								Object.class,
								long.class
							}
						);

						// Object getObjectVolatile(Object obj, long offset);
						method.accept(
							new String[]{
								"getObjectVolatile",
								majorVersion > 0x37 ? "getReferenceVolatile" : "getObjectVolatile"
							},
							new Class[]{
								Object.class,
								Object.class,
								long.class
							}
						);

						// Object getReferenceVolatile(Object obj, long offset);
						method.accept(
							new String[]{
								"getReferenceVolatile",
								majorVersion > 0x37 ? "getReferenceVolatile" : "getObjectVolatile"
							},
							new Class[]{
								Object.class,
								Object.class,
								long.class
							}
						);

						// void putObject(Object obj, long offset, Object value);
						method.accept(
							new String[]{
								"putObject",
								majorVersion > 0x37 ? "putReference" : "putObject"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								Object.class
							}
						);

						// void putReference(Object obj, long offset, Object value);
						method.accept(
							new String[]{
								"putReference",
								majorVersion > 0x37 ? "putReference" : "putObject"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								Object.class
							}
						);

						// void putObjectVolatile(Object obj, long offset, Object value);
						method.accept(
							new String[]{
								"putObjectVolatile",
								majorVersion > 0x37 ? "putReferenceVolatile" : "putObjectVolatile"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								Object.class
							}
						);

						// void putReferenceVolatile(Object obj, long offset, Object value);
						method.accept(
							new String[]{
								"putReferenceVolatile",
								majorVersion > 0x37 ? "putReferenceVolatile" : "putObjectVolatile"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								Object.class
							}
						);

						// long getAddress(long address);
						method.accept(
							new String[]{
								"getAddress",
								"getAddress"
							},
							new Class[]{
								long.class,
								long.class
							}
						);

						// void putAddress(long address, long value);
						method.accept(
							new String[]{
								"putAddress",
								"putAddress"
							},
							new Class[]{
								void.class,
								long.class
							}
						);

						// long allocateMemory(long length);
						method.accept(
							new String[]{
								"allocateMemory",
								"allocateMemory"
							},
							new Class[]{
								long.class,
								long.class
							}
						);

						// long reallocateMemory(long address, long length);
						method.accept(
							new String[]{
								"reallocateMemory",
								"reallocateMemory"
							},
							new Class[]{
								long.class,
								long.class,
								long.class
							}
						);

						// void setMemory(Object o, long offset, long bytes, byte value);
						method.accept(
							new String[]{
								"setMemory",
								"setMemory"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								long.class,
								byte.class
							}
						);

						// void setMemory(long address, long bytes, byte value);
						method.accept(
							new String[]{
								"setMemory",
								"setMemory"
							},
							new Class[]{
								void.class,
								long.class,
								long.class,
								byte.class
							}
						);

						// void copyMemory(Object src, long secOff, Object dest, long destOff, long length);
						method.accept(
							new String[]{
								"copyMemory",
								"copyMemory"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								Object.class,
								long.class,
								long.class
							}
						);

						// void copyMemory(long sec, long dest, long length);
						method.accept(
							new String[]{
								"copyMemory",
								"copyMemory"
							},
							new Class[]{
								void.class,
								long.class,
								long.class
							}
						);

						// void freeMemory(long address);
						method.accept(
							new String[]{
								"freeMemory",
								"freeMemory"
							},
							new Class[]{
								void.class,
								long.class
							}
						);

						// long staticFieldOffset(Field f);
						method.accept(
							new String[]{
								"staticFieldOffset",
								"staticFieldOffset"
							},
							new Class[]{
								long.class,
								Field.class
							}
						);

						// long objectFieldOffset(Field f);
						method.accept(
							new String[]{
								"objectFieldOffset",
								"objectFieldOffset"
							},
							new Class[]{
								long.class,
								Field.class
							}
						);

						// Object staticFieldBase(Field f);
						method.accept(
							new String[]{
								"staticFieldBase",
								"staticFieldBase"
							},
							new Class[]{
								Object.class,
								Field.class
							}
						);

						// boolean shouldBeInitialized(Class<?> c);
						method.accept(
							new String[]{
								"shouldBeInitialized",
								"shouldBeInitialized",
								"(Ljava/lang/Class<*>;)Z"
							},
							new Class[]{
								boolean.class,
								Class.class
							}
						);

						// void ensureClassInitialized(Class<?> c);
						method.accept(
							new String[]{
								"ensureClassInitialized",
								"ensureClassInitialized",
								"(Ljava/lang/Class<*>;)V"
							},
							new Class[]{
								void.class,
								Class.class
							}
						);

						// int arrayBaseOffset(Class<?> c);
						method.accept(
							new String[]{
								"arrayBaseOffset",
								"arrayBaseOffset",
								"(Ljava/lang/Class<8>;)I"
							},
							new Class[]{
								int.class,
								Class.class
							}
						);

						// int arrayIndexScale(Class<?> c);
						method.accept(
							new String[]{
								"arrayIndexScale",
								"arrayIndexScale",
								"(Ljava/lang/Class<*>;)I"
							},
							new Class[]{
								int.class,
								Class.class
							}
						);

						// int addressSize();
						method.accept(
							new String[]{
								"addressSize",
								"addressSize"
							},
							new Class[]{
								int.class
							}
						);

						// int pageSize();
						method.accept(
							new String[]{
								"pageSize",
								"pageSize"
							},
							new Class[]{
								int.class
							}
						);

						// Class<?> defineClass(String name, byte[] code, int offset, int length, ClassLoader loader, ProtectionDomain protectionDomain);
						method.accept(
							new String[]{
								"defineClass",
								"defineClass",
								"(Ljava/lang/String;[BIILjava/lang/ClassLoader;Ljava/security/ProtectionDomain;)Ljava/lang/Class<*>;"
							},
							new Class[]{
								Class.class,
								String.class,
								byte[].class,
								int.class,
								int.class,
								ClassLoader.class,
								ProtectionDomain.class
							}
						);

						// Class<?> defineAnonymousClass(Class<?> hostClass, byte[] data, Object[] cpPatches);
						method.accept(
							new String[]{
								"defineAnonymousClass",
								"defineAnonymousClass",
								"(Ljava/lang/Class<*>;[B[Ljava/lang/Object;)Ljava/lang/Class<*>;"
							},
							new Class[]{
								Class.class,
								Class.class,
								byte[].class,
								Object[].class
							}
						);

						// Object allocateInstance(Class<?> c);
						method.accept(
							new String[]{
								"allocateInstance",
								"allocateInstance",
								"(Ljava/lang/Class<*>;)Ljava/lang/Object;"
							},
							new Class[]{
								Object.class,
								Class.class
							}
						);

						// void throwException(Throwable t);
						method.accept(
							new String[]{
								"throwException",
								"throwException"
							},
							new Class[]{
								void.class,
								Throwable.class
							}
						);

						// boolean compareAndSwapInt(Object obj, long offset, int expected, int value);
						method.accept(
							new String[]{
								"compareAndSwapInt",
								majorVersion > 0x35 ? "compareAndSetInt" : "compareAndSwapInt"
							},
							new Class[]{
								boolean.class,
								Object.class,
								long.class,
								int.class,
								int.class
							}
						);

						// boolean compareAndSetInt(Object obj, long offset, int expected, int value);
						method.accept(
							new String[]{
								"compareAndSetInt",
								majorVersion > 0x35 ? "compareAndSetInt" : "compareAndSwapInt"
							},
							new Class[]{
								boolean.class,
								Object.class,
								long.class,
								int.class,
								int.class
							}
						);

						// boolean compareAndSwapLong(Object obj, long offset, long expected, long value);
						method.accept(
							new String[]{
								"compareAndSwapLong",
								majorVersion > 0x35 ? "compareAndSetLong" : "compareAndSwapLong"
							},
							new Class[]{
								boolean.class,
								Object.class,
								long.class,
								long.class,
								long.class
							}
						);

						// boolean compareAndSetLong(Object obj, long offset, long expected, long value);
						method.accept(
							new String[]{
								"compareAndSetLong",
								majorVersion > 0x35 ? "compareAndSetLong" : "compareAndSwapLong"
							},
							new Class[]{
								boolean.class,
								Object.class,
								long.class,
								long.class,
								long.class
							}
						);

						// boolean compareAndSwapObject(Object obj, long offset, Object expected, Object value);
						method.accept(
							new String[]{
								"compareAndSwapObject",
								majorVersion > 0x35 ? majorVersion <= 0x37 ? "compareAndSetObject" : "compareAndSetReference" : "compareAndSwapObject"
							},
							new Class[]{
								boolean.class,
								Object.class,
								long.class,
								Object.class,
								Object.class
							}
						);

						// boolean compareAndSetObject(Object obj, long offset, Object expected, Object value);
						method.accept(
							new String[]{
								"compareAndSetObject",
								majorVersion > 0x35 ? majorVersion <= 0x37 ? "compareAndSetObject" : "compareAndSetReference" : "compareAndSwapObject"
							},
							new Class[]{
								boolean.class,
								Object.class,
								long.class,
								Object.class,
								Object.class
							}
						);

						// boolean compareAndSetReference(Object obj, long offset, Object expected, Object value);
						method.accept(
							new String[]{
								"compareAndSetReference",
								majorVersion > 0x35 ? majorVersion <= 0x37 ? "compareAndSetObject" : "compareAndSetReference" : "compareAndSwapObject"
							},
							new Class[]{
								boolean.class,
								Object.class,
								long.class,
								Object.class,
								Object.class
							}
						);

						// void putOrderedInt(Object o, long offset, int x);
						method.accept(
							new String[]{
								"putOrderedInt",
								majorVersion > 0x34 ? "putIntRelease" : "putOrderedInt"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								int.class
							}
						);

						// void putIntRelease(Object o, long offset, int x);
						method.accept(
							new String[]{
								"putIntRelease",
								majorVersion > 0x34 ? "putIntRelease" : "putOrderedInt"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								int.class
							}
						);

						// void putOrderedLong(Object o, long offset, long x);
						method.accept(
							new String[]{
								"putOrderedLong",
								majorVersion > 0x34 ? "putLongRelease" : "putOrderedLong"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								long.class
							}
						);

						// void putLongRelease(Object o, long offset, long x);
						method.accept(
							new String[]{
								"putLongRelease",
								majorVersion > 0x34 ? "putLongRelease" : "putOrderedLong"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								long.class
							}
						);

						// void putOrderedObject(Object o, long offset, Object x);
						method.accept(
							new String[]{
								"putOrderedObject",
								majorVersion != 0x34 ? majorVersion <= 0x37 ? "putObjectRelease" : "putReferenceRelease" : "putOrderedObject"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								long.class
							}
						);

						// void putObjectRelease(Object o, long offset, Object x);
						method.accept(
							new String[]{
								"putObjectRelease",
								majorVersion != 0x34 ? majorVersion <= 0x37 ? "putObjectRelease" : "putReferenceRelease" : "putOrderedObject"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								long.class
							}
						);

						// void putReferenceRelease(Object o, long offset, Object x);
						method.accept(
							new String[]{
								"putReferenceRelease",
								majorVersion != 0x34 ? majorVersion <= 0x37 ? "putObjectRelease" : "putReferenceRelease" : "putOrderedObject"
							},
							new Class[]{
								void.class,
								Object.class,
								long.class,
								long.class
							}
						);

						// void unpark(Object thread);
						method.accept(
							new String[]{
								"unpark",
								"unpark"
							},
							new Class[]{
								void.class,
								Object.class
							}
						);

						// void park(boolean isAbsolute, long time);
						method.accept(
							new String[]{
								"park",
								"park"
							},
							new Class[]{
								void.class,
								boolean.class,
								long.class
							}
						);

						// int getLoadAverage(double[] loadavg, int nelems);
						method.accept(
							new String[]{
								"getLoadAverage",
								"getLoadAverage"
							},
							new Class[]{
								int.class,
								double[].class,
								int.class
							}
						);

						// int getAndAddInt(Object o, long offset, int delta);
						method.accept(
							new String[]{
								"getAndAddInt",
								"getAndAddInt"
							},
							new Class[]{
								int.class,
								Object.class,
								long.class,
								int.class
							}
						);

						// int getAndSetInt(Object o, long offset, int newValue);
						method.accept(
							new String[]{
								"getAndSetInt",
								"getAndSetInt"
							},
							new Class[]{
								int.class,
								Object.class,
								long.class,
								int.class
							}
						);

						// long getAndAddLong(Object o, long offset, long delta);
						method.accept(
							new String[]{
								"getAndAddLong",
								"getAndAddLong"
							},
							new Class[]{
								long.class,
								Object.class,
								long.class,
								long.class
							}
						);

						// long getAndSetLong(Object o, long offset, long newValue);
						method.accept(
							new String[]{
								"getAndSetLong",
								"getAndSetLong"
							},
							new Class[]{
								long.class,
								Object.class,
								long.class,
								long.class
							}
						);

						// Object getAndSetObject(Object o, long offset, Object newValue);
						method.accept(
							new String[]{
								"getAndSetObject",
								majorVersion > 0x37 ? "getAndSetReference" : "getAndSetObject"
							},
							new Class[]{
								Object.class,
								Object.class,
								long.class,
								Object.class
							}
						);

						// Object getAndSetReference(Object o, long offset, Object newValue);
						method.accept(
							new String[]{
								"getAndSetReference",
								majorVersion > 0x37 ? "getAndSetReference" : "getAndSetObject"
							},
							new Class[]{
								Object.class,
								Object.class,
								long.class,
								Object.class
							}
						);

						// void loadFence();
						method.accept(
							new String[]{
								"loadFence",
								"loadFence"
							},
							new Class[]{
								void.class
							}
						);

						// void storeFence();
						method.accept(
							new String[]{
								"storeFence",
								"storeFence"
							},
							new Class[]{
								void.class
							}
						);

						// void fullFence();
						method.accept(
							new String[]{
								"fullFence",
								"fullFence"
							},
							new Class[]{
								void.class
							}
						);

						// void loadLoadFence();
						Class<?>[] arr = new Class[]{void.class};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"loadLoadFence"}, arr);
						else method.accept(
							new String[]{
								"loadLoadFence",
								"loadLoadFence"
							},
							arr
						);

						// void storeStoreFence();
						if (majorVersion < 0x35) unsupported.accept(new String[]{"storeStoreFence"}, arr);
						else method.accept(
							new String[]{
								"storeStoreFence",
								"storeStoreFence"
							},
							arr
						);

						// boolean unalignedAccess();
						arr = new Class[]{boolean.class};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"unalignedAccess"}, arr);
						else method.accept(
							new String[]{
								"unalignedAccess",
								"unalignedAccess"
							},
							arr
						);

						// long getAddress(Object o, long offset)
						arr = new Class[]{
							long.class,
							Object.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getAddress"}, arr);
						else method.accept(
							new String[]{
								"getAddress",
								"getAddress"
							},
							arr
						);

						// void putAddress(Object o, long offset, long x)
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putAddress"}, arr);
						else method.accept(
							new String[]{
								"putAddress",
								"putAddress"
							},
							arr
						);

						// Object getUncompressedObject(long address);
						arr = new Class[]{
							Object.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getUncompressedObject"}, arr);
						else method.accept(
							new String[]{
								"getUncompressedObject",
								"getUncompressedObject"
							},
							arr
						);

						// void copySwapMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes, long elemSize);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							Object.class,
							long.class,
							long.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"copySwapMemory"}, arr);
						else method.accept(
							new String[]{
								"copySwapMemory",
								"copySwapMemory"
							},
							arr
						);

						// void copySwapMemory(long srcAddress, long destAddress, long bytes, long elemSize)
						arr = new Class[]{
							void.class,
							long.class,
							long.class,
							long.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"copySwapMemory"}, arr);
						else method.accept(
							new String[]{
								"copySwapMemory",
								"copySwapMemory"
							},
							arr
						);

						// Class<?> defineClass0(String name, byte[] b, int off, int len, ClassLoader loader, ProtectionDomain protectionDomain);
						arr = new Class[]{
							Class.class,
							String.class,
							byte[].class,
							int.class,
							int.class,
							ClassLoader.class,
							ProtectionDomain.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"defineClass0", "(Ljava/lang/String;[BIILjava/lang/ClassLoader;Ljava/security/ProtectionDomain;)Ljava/lang/Class<*>;"}, arr);
						else method.accept(
							new String[]{
								"defineClass0",
								"defineClass0",
								"(Ljava/lang/String;[BIILjava/lang/ClassLoader;Ljava/security/ProtectionDomain;)Ljava/lang/Class<?>;"
							},
							arr
						);

						// Object allocateUninitializedArray(Class<?> componentType, int length);
						arr = new Class[]{
							Object.class,
							Class.class,
							int.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"allocateUninitializedArray", "(Ljava/lang/Class<*>;I)Ljava/lang/Object;"}, arr);
						else method.accept(
							new String[]{
								"allocateUninitializedArray",
								"allocateUninitializedArray",
								"(Ljava/lang/Class<*>;I)Ljava/lang/Object;"
							},
							arr
						);

						// boolean isBigEndian();
						arr = new Class[]{boolean.class};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"isBigEndian"}, arr);
						else method.accept(
							new String[]{
								"isBigEndian",
								"isBigEndian"
							},
							arr
						);

						// byte getAndAddByte(Object o, long offset, byte delta);
						arr = new Class[]{
							byte.class,
							Object.class,
							long.class,
							byte.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndAddByte"}, arr);
						else method.accept(
							new String[]{
								"getAndAddByte",
								"getAndAddByte"
							},
							arr
						);

						// byte getAndSetByte(Object o, long offset, byte newValue);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndSetByte"}, arr);
						else method.accept(
							new String[]{
								"getAndSetByte",
								"getAndSetByte"
							},
							arr
						);

						// short getAndAddShort(Object o, long offset, short delta);
						arr = new Class[]{
							short.class,
							Object.class,
							long.class,
							short.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndAddShort"}, arr);
						else method.accept(
							new String[]{
								"getAndAddShort",
								"getAndAddShort"
							},
							arr
						);

						// short getAndSetShort(Object o, long offset, short newValue);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndSetShort"}, arr);
						else method.accept(
							new String[]{
								"getAndSetShort",
								"getAndSetShort"
							},
							arr
						);

						// Object getObjectOpaque(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getObjectOpaque"}, new Class[]{Object.class, Object.class, long.class});
						else method.accept(
							new String[]{
								"getObjectOpaque",
								majorVersion > 0x37 ? "putReferenceOpaque" : "getObjectOpaque"
							},
							new Class[]{
								Object.class,
								Object.class,
								long.class
							}
						);

						// Object getObjectAcquire(Object o, long offset);
						arr = new Class[]{
							Object.class,
							Object.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getObjectAcquire"}, arr);
						else method.accept(
							new String[]{
								"getObjectAcquire",
								majorVersion > 0x37 ? "getReferenceAcquire" : "getObjectAcquire"
							},
							arr
						);

						// Object getReferenceAcquire(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getReferenceAcquire"}, arr);
						else method.accept(
							new String[]{
								"getReferenceAcquire",
								majorVersion > 0x37 ? "getReferenceAcquire" : "getObjectAcquire"
							},
							arr
						);

						// Object getReferenceOpaque(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getReferenceOpaque"}, arr);
						else method.accept(
							new String[]{
								"getReferenceOpaque",
								majorVersion > 0x37 ? "getReferenceOpaque" : "getObjectOpaque"
							},
							arr
						);

						// boolean getBooleanOpaque(Object o, long offset);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getBooleanOpaque"}, arr);
						else method.accept(
							new String[]{
								"getBooleanOpaque",
								"getBooleanOpaque"
							},
							arr
						);

						// boolean getBooleanAcquire(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getBooleanAcquire"}, arr);
						else method.accept(
							new String[]{
								"getBooleanAcquire",
								"getBooleanAcquire"
							},
							arr
						);

						// void putBooleanOpaque(Object o, long offset, boolean x);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							boolean.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putBooleanOpaque"}, arr);
						else method.accept(
							new String[]{
								"putBooleanOpaque",
								"putBooleanOpaque"
							},
							arr
						);

						// void putBooleanRelease(Object o, long offset, boolean x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putBooleanRelease"}, arr);
						else method.accept(
							new String[]{
								"putBooleanRelease",
								"putBooleanRelease"
							},
							arr
						);

						// byte getByteOpaque(Object o, long offset);
						arr = new Class[]{
							byte.class,
							Object.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getByteOpaque"}, arr);
						else method.accept(
							new String[]{
								"getByteOpaque",
								"getByteOpaque"
							},
							arr
						);

						// byte getByteAcquire(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getByteAcquire"}, arr);
						else method.accept(
							new String[]{
								"getByteAcquire",
								"getByteAcquire"
							},
							arr
						);

						// void putByteOpaque(Object o, long offset, byte x);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							byte.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putByteOpaque"}, arr);
						else method.accept(
							new String[]{
								"putByteOpaque",
								"putByteOpaque"
							},
							arr
						);

						// void putByteRelease(Object o, long offset, byte x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putByteRelease"}, arr);
						else method.accept(
							new String[]{
								"putByteRelease",
								"putByteRelease"
							},
							arr
						);

						// short getShortOpaque(Object o, long offset);
						arr = new Class[]{
							short.class,
							Object.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getShortOpaque"}, arr);
						else method.accept(
							new String[]{
								"getShortOpaque",
								"getShortOpaque"
							},
							arr
						);

						// short getShortAcquire(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getShortAcquire"}, arr);
						else method.accept(
							new String[]{
								"getShortAcquire",
								"getShortAcquire"
							},
							arr
						);

						// short getShortUnaligned(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getShortUnaligned"}, arr);
						else method.accept(
							new String[]{
								"getShortUnaligned",
								"getShortUnaligned"
							},
							arr
						);

						// short getShortUnaligned(Object o, long offset, boolean bigEndian)
						arr = new Class[]{
							short.class,
							Object.class,
							long.class,
							boolean.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getShortUnaligned"}, arr);
						else method.accept(
							new String[]{
								"getShortUnaligned",
								"getShortUnaligned"
							},
							arr
						);

						// void putShortOpaque(Object o, long offset, short x);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							short.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putShortOpaque"}, arr);
						else method.accept(
							new String[]{
								"putShortOpaque",
								"putShortOpaque"
							},
							arr
						);

						// void putShortRelease(Object o, long offset, short x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putShortRelease"}, arr);
						else method.accept(
							new String[]{
								"putShortRelease",
								"putShortRelease"
							},
							arr
						);

						// void putShortUnaligned(Object o, long offset, short x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putShortUnaligned"}, arr);
						else method.accept(
							new String[]{
								"putShortUnaligned",
								"putShortUnaligned"
							},
							arr
						);

						// void putShortUnaligned(Object o, long offset, short x, boolean bigEndian);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							short.class,
							boolean.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putShortUnaligned"}, arr);
						else method.accept(
							new String[]{
								"putShortUnaligned",
								"putShortUnaligned"
							},
							arr
						);

						// char getCharOpaque(Object o, long offset);
						arr = new Class[]{
							char.class,
							Object.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getCharOpaque"}, arr);
						else method.accept(
							new String[]{
								"getCharOpaque",
								"getCharOpaque"
							},
							arr
						);

						// char getCharAcquire(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getCharAcquire"}, arr);
						else method.accept(
							new String[]{
								"getCharAcquire",
								"getCharAcquire"
							},
							arr
						);

						// char getCharUnaligned(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getCharUnaligned"}, arr);
						else method.accept(
							new String[]{
								"getCharUnaligned",
								"getCharUnaligned"
							},
							arr
						);

						// char getCharUnaligned(Object o, long offset, boolean bigEndian);
						arr = new Class[]{
							char.class,
							Object.class,
							long.class,
							boolean.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getCharUnaligned"}, arr);
						else method.accept(
							new String[]{
								"getCharUnaligned",
								"getCharUnaligned"
							},
							arr
						);

						// void putCharOpaque(Object o, long offset, char x);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							char.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putCharOpaque"}, arr);
						else method.accept(
							new String[]{
								"putCharOpaque",
								"putCharOpaque"
							},
							arr
						);

						// void putCharRelease(Object o, long offset, char x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putCharRelease"}, arr);
						else method.accept(
							new String[]{
								"putCharRelease",
								"putCharRelease"
							},
							arr
						);

						// void putCharUnaligned(Object o, long offset, char x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putCharUnaligned"}, arr);
						else method.accept(
							new String[]{
								"putCharUnaligned",
								"putCharUnaligned"
							},
							arr
						);

						// void putCharUnaligned(Object o, long offset, char x, boolean bigEndian);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							char.class,
							boolean.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putCharUnaligned"}, arr);
						else method.accept(
							new String[]{
								"putCharUnaligned",
								"putCharUnaligned"
							},
							arr
						);

						// int getIntOpaque(Object o, long offset);
						arr = new Class[]{
							int.class,
							Object.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getIntOpaque"}, arr);
						else method.accept(
							new String[]{
								"getIntOpaque",
								"getIntOpaque"
							},
							arr
						);

						// int getIntAcquire(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getIntAcquire"}, arr);
						else method.accept(
							new String[]{
								"getIntAcquire",
								"getIntAcquire"
							},
							arr
						);

						// int getIntUnaligned(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getIntUnaligned"}, arr);
						else method.accept(
							new String[]{
								"getIntUnaligned",
								"getIntUnaligned"
							},
							arr
						);

						// int getIntUnaligned(Object o, long offset, boolean bigEndian);
						arr = new Class[]{
							int.class,
							Object.class,
							long.class,
							boolean.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getIntUnaligned"}, arr);
						else method.accept(
							new String[]{
								"getIntUnaligned",
								"getIntUnaligned"
							},
							arr
						);

						// void putIntOpaque(Object o, long offset, int x);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							int.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putIntOpaque"}, arr);
						else method.accept(
							new String[]{
								"putIntOpaque",
								"putIntOpaque"
							},
							arr
						);

						// void putIntUnaligned(Object o, long offset, int x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putIntUnaligned"}, arr);
						else method.accept(
							new String[]{
								"putIntUnaligned",
								"putIntUnaligned"
							},
							arr
						);

						// void putIntUnaligned(Object o, long offset, int x, boolean bigEndian);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							int.class,
							boolean.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putIntUnaligned"}, arr);
						else method.accept(
							new String[]{
								"putIntUnaligned",
								"putIntUnaligned"
							},
							arr
						);

						// long getLongOpaque(Object o, long offset);
						arr = new Class[]{
							long.class,
							Object.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getLongOpaque"}, arr);
						else method.accept(
							new String[]{
								"getLongOpaque",
								"getLongOpaque"
							},
							arr
						);

						// long getLongAcquire(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getLongAcquire"}, arr);
						else method.accept(
							new String[]{
								"getLongAcquire",
								"getLongAcquire"
							},
							arr
						);

						// long getLongUnaligned(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getLongUnaligned"}, arr);
						else method.accept(
							new String[]{
								"getLongUnaligned",
								"getLongUnaligned"
							},
							arr
						);

						// long getLongUnaligned(Object o, long offset, boolean bigEndian);
						arr = new Class[]{
							long.class,
							Object.class,
							long.class,
							boolean.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getLongUnaligned"}, arr);
						else method.accept(
							new String[]{
								"getLongUnaligned",
								"getLongUnaligned"
							},
							arr
						);

						// void putLongOpaque(Object o, long offset, long x);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putLongOpaque"}, arr);
						else method.accept(
							new String[]{
								"putLongOpaque",
								"putLongOpaque"
							},
							arr
						);

						// void putLongUnaligned(Object o, long offset, long x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putLongUnaligned"}, arr);
						else method.accept(
							new String[]{
								"putLongUnaligned",
								"putLongUnaligned"
							},
							arr
						);

						// void putLongUnaligned(Object o, long offset, long x, boolean bigEndian);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							long.class,
							boolean.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putLongUnaligned"}, arr);
						else method.accept(
							new String[]{
								"putLongUnaligned",
								"putLongUnaligned"
							},
							arr
						);

						// float getFloatOpaque(Object o, long offset);
						arr = new Class[]{
							float.class,
							Object.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getFloatOpaque"}, arr);
						else method.accept(
							new String[]{
								"getFloatOpaque",
								"getFloatOpaque"
							},
							arr
						);

						// float getFloatAcquire(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getFloatAcquire"}, arr);
						else method.accept(
							new String[]{
								"getFloatAcquire",
								"getFloatAcquire"
							},
							arr
						);

						// void putFloatOpaque(Object o, long offset, float x);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							float.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putFloatOpaque"}, arr);
						else method.accept(
							new String[]{
								"putFloatOpaque",
								"putFloatOpaque"
							},
							arr
						);

						// void putFloatRelease(Object o, long offset, float x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putFloatRelease"}, arr);
						else method.accept(
							new String[]{
								"putFloatRelease",
								"putFloatRelease"
							},
							arr
						);

						// double getDoubleOpaque(Object o, long offset);
						arr = new Class[]{
							double.class,
							Object.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getDoubleOpaque"}, arr);
						else method.accept(
							new String[]{
								"getDoubleOpaque",
								"getDoubleOpaque"
							},
							arr
						);

						// double getDoubleAcquire(Object o, long offset);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getDoubleAcquire"}, arr);
						else method.accept(
							new String[]{
								"getDoubleAcquire",
								"getDoubleAcquire"
							},
							arr
						);

						// void putObjectOpaque(Object o, long offset, Object x);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							Object.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putObjectOpaque"}, arr);
						else method.accept(
							new String[]{
								"putObjectOpaque",
								"putObjectOpaque"
							},
							arr
						);

						// void putReferenceOpaque(Object o, long offset, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putReferenceOpaque"}, arr);
						else method.accept(
							new String[]{
								"putReferenceOpaque",
								"putReferenceOpaque"
							},
							arr
						);

						// void putDoubleOpaque(Object o, long offset, double x);
						arr = new Class[]{
							void.class,
							Object.class,
							long.class,
							double.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putDoubleOpaque"}, arr);
						else method.accept(
							new String[]{
								"putDoubleOpaque",
								"putDoubleOpaque"
							},
							arr
						);

						// void putDoubleRelease(Object o, long offset, double x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"putDoubleRelease"}, arr);
						else method.accept(
							new String[]{
								"putDoubleRelease",
								"putDoubleRelease"
							},
							arr
						);

						// Object compareAndExchangeObject(Object o, long offset, Object expected, Object x);
						arr = new Class[]{
							Object.class,
							Object.class,
							long.class,
							Object.class,
							Object.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeObject"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeObject",
								majorVersion > 0x37 ? "compareAndExchangeReference" : "compareAndExchangeObject"
							},
							arr
						);

						// Object compareAndExchangeReference(Object o, long offset, Object expected, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeReference"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeReference",
								majorVersion > 0x37 ? "compareAndExchangeReference" : "compareAndExchangeObject"
							},
							arr
						);

						// Object compareAndExchangeObjectAcquire(Object o, long offset, Object expected, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeObjectAcquire"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeObjectAcquire",
								majorVersion > 0x37 ? "compareAndExchangeReferenceAcquire" : "compareAndExchangeObjectAcquire"
							},
							arr
						);

						// Object compareAndExchangeReferenceAcquire(Object o, long offset, Object expected, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeReferenceAcquire"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeReferenceAcquire",
								majorVersion > 0x37 ? "compareAndExchangeReferenceAcquire" : "compareAndExchangeObjectAcquire"
							},
							arr
						);

						//  Object compareAndExchangeObjectRelease(Object o, long offset, Object expected, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeObjectRelease"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeObjectRelease",
								majorVersion > 0x37 ? "compareAndExchangeReferenceRelease" : "compareAndExchangeObjectRelease"
							},
							arr
						);

						// Object compareAndExchangeReferenceRelease(Object o, long offset, Object expected, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeReferenceRelease"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeReferenceRelease",
								majorVersion > 0x37 ? "compareAndExchangeReferenceRelease" : "compareAndExchangeObjectRelease"
							},
							arr
						);

						// long compareAndExchangeLong(Object o, long offset, long expected, long x);
						arr = new Class[]{
							long.class,
							Object.class,
							long.class,
							long.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeLong"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeLong",
								"compareAndExchangeLong"
							},
							arr
						);

						// long compareAndExchangeLongAcquire(Object o, long offset, long expected, long x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeLongAcquire"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeLongAcquire",
								"compareAndExchangeLongAcquire"
							},
							arr
						);

						// long compareAndExchangeLongRelease(Object o, long offset, long expected, long x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeLongRelease"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeLongRelease",
								"compareAndExchangeLongRelease"
							},
							arr
						);

						// int compareAndExchangeInt(Object o, long offset, int expected, int x);
						arr = new Class[]{
							int.class,
							Object.class,
							long.class,
							int.class,
							int.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeInt"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeInt",
								"compareAndExchangeInt"
							},
							arr
						);

						// int compareAndExchangeIntAcquire(Object o, long offset, int expected, int x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeIntAcquire"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeIntAcquire",
								"compareAndExchangeIntAcquire"
							},
							arr
						);

						// int compareAndExchangeIntRelease(Object o, long offset, int expected, int x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeIntRelease"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeIntRelease",
								"compareAndExchangeIntRelease"
							},
							arr
						);

						// boolean compareAndSetByte(Object o, long offset, byte expected, byte x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							byte.class,
							byte.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndSetByte"}, arr);
						else method.accept(
							new String[]{
								"compareAndSetByte",
								"compareAndSetByte"
							},
							arr
						);

						// byte compareAndExchangeByte(Object o, long offset, byte expected, byte x);
						arr = new Class[]{
							byte.class,
							Object.class,
							long.class,
							byte.class,
							byte.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeByte"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeByte",
								"compareAndExchangeByte"
							},
							arr
						);

						// byte compareAndExchangeByteAcquire(Object o, long offset, byte expected, byte x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeByteAcquire"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeByteAcquire",
								"compareAndExchangeByteAcquire"
							},
							arr
						);

						// byte compareAndExchangeByteRelease(Object o, long offset, byte expected, byte x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeByteRelease"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeByteRelease",
								"compareAndExchangeByteRelease"
							},
							arr
						);

						// boolean compareAndSetShort(Object o, long offset, short expected, short x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							short.class,
							short.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndSetShort"}, arr);
						else method.accept(
							new String[]{
								"compareAndSetShort",
								"compareAndSetShort"
							},
							arr
						);

						// short compareAndExchangeShort(Object o, long offset, short expected, short x);
						arr = new Class[]{
							short.class,
							Object.class,
							long.class,
							short.class,
							short.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeShort"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeShort",
								"compareAndExchangeShort"
							},
							arr
						);

						// short compareAndExchangeShortAcquire(Object o, long offset, short expected, short x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeShortAcquire"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeShortAcquire",
								"compareAndExchangeShortAcquire"
							},
							arr
						);

						// short compareAndExchangeShortRelease(Object o, long offset, short expected, short x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeShortRelease"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeShortRelease",
								"compareAndExchangeShortRelease"
							},
							arr
						);

						// boolean compareAndSetChar(Object o, long offset, char expected, char x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							char.class,
							char.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndSetChar"}, arr);
						else method.accept(
							new String[]{
								"compareAndSetChar",
								"compareAndSetChar"
							},
							arr
						);

						// char compareAndExchangeChar(Object o, long offset, char expected, char x);
						arr = new Class[]{
							char.class,
							Object.class,
							long.class,
							char.class,
							char.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeChar"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeChar",
								"compareAndExchangeChar"
							},
							arr
						);

						// char compareAndExchangeCharAcquire(Object o, long offset, char expected, char x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeCharAcquire"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeCharAcquire",
								"compareAndExchangeCharAcquire"
							},
							arr
						);

						// char compareAndExchangeCharRelease(Object o, long offset, char expected, char x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeCharRelease"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeCharRelease",
								"compareAndExchangeCharRelease"
							},
							arr
						);

						// boolean compareAndSetFloat(Object o, long offset, float expected, float x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							float.class,
							float.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndSetFloat"}, arr);
						else method.accept(
							new String[]{
								"compareAndSetFloat",
								"compareAndSetFloat"
							},
							arr
						);

						// float compareAndExchangeFloat(Object o, long offset, float expected, float x);
						arr = new Class[]{
							float.class,
							Object.class,
							long.class,
							float.class,
							float.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeFloat"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeFloat",
								"compareAndExchangeFloat"
							},
							arr
						);

						// float compareAndExchangeFloatAcquire(Object o, long offset, float expected, float x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeFloatAcquire"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeFloatAcquire",
								"compareAndExchangeFloatAcquire"
							},
							arr
						);

						// float compareAndExchangeFloatRelease(Object o, long offset, float expected, float x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeFloatRelease"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeFloatRelease",
								"compareAndExchangeFloatRelease"
							},
							arr
						);

						// boolean compareAndSetBoolean(Object o, long offset, boolean expected, boolean x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							boolean.class,
							boolean.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndSetBoolean"}, arr);
						else method.accept(
							new String[]{
								"compareAndSetBoolean",
								"compareAndSetBoolean"
							},
							arr
						);

						// boolean compareAndExchangeBoolean(Object o, long offset, boolean expected, boolean x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeBoolean"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeBoolean",
								"compareAndExchangeBoolean"
							},
							arr
						);

						// boolean compareAndExchangeBooleanAcquire(Object o, long offset, boolean expected, boolean x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeBooleanAcquire"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeBooleanAcquire",
								"compareAndExchangeBooleanAcquire"
							},
							arr
						);

						// boolean compareAndExchangeBooleanRelease(Object o, long offset, boolean expected, boolean x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeBooleanRelease"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeBooleanRelease",
								"compareAndExchangeBooleanRelease"
							},
							arr
						);

						// boolean compareAndSetDouble(Object o, long offset, double expected, double x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							double.class,
							double.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndSetDouble"}, arr);
						else method.accept(
							new String[]{
								"compareAndSetDouble",
								"compareAndSetDouble"
							},
							arr
						);

						// double compareAndExchangeDouble(Object o, long offset, double expected, double x);
						arr = new Class[]{
							double.class,
							Object.class,
							long.class,
							double.class,
							double.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeDouble"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeDouble",
								"compareAndExchangeDouble"
							},
							arr
						);

						// double compareAndExchangeDoubleAcquire(Object o, long offset, double expected, double x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeDoubleAcquire"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeDoubleAcquire",
								"compareAndExchangeDoubleAcquire"
							},
							arr
						);

						// double compareAndExchangeDoubleRelease(Object o, long offset, double expected, double x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeDoubleRelease"}, arr);
						else method.accept(
							new String[]{
								"compareAndExchangeDoubleRelease",
								"compareAndExchangeDoubleRelease"
							},
							arr
						);

						// boolean weakCompareAndSetObject(Object o, long offset, Object expected, Object x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							Object.class,
							Object.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetObject"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetObject",
								majorVersion > 0x37 ? "weakCompareAndSetReference" : "weakCompareAndSetObject"
							},
							arr
						);

						// boolean weakCompareAndSetReference(Object o, long offset, Object expected, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetReference"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetReference",
								majorVersion > 0x37 ? "weakCompareAndSetReference" : "weakCompareAndSetObject"
							},
							arr
						);

						// boolean weakCompareAndSetObjectPlain(Object o, long offset, Object expected, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetObjectPlain"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetObjectPlain",
								majorVersion > 0x37 ? "weakCompareAndSetReferencePlain" : "weakCompareAndSetObjectPlain"
							},
							arr
						);

						// boolean weakCompareAndSetReferencePlain(Object o, long offset, Object expected, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetReferencePlain"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetReferencePlain",
								majorVersion > 0x37 ? "weakCompareAndSetReferencePlain" : "weakCompareAndSetObjectPlain"
							},
							arr
						);

						// boolean weakCompareAndSetObjectAcquire(Object o, long offset, Object expected, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetObjectAcquire"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetObjectAcquire",
								majorVersion > 0x37 ? "weakCompareAndSetReferenceAcquire" : "weakCompareAndSetObjectAcquire"
							},
							arr
						);

						// boolean weakCompareAndSetReferenceAcquire(Object o, long offset, Object expected, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetReferenceAcquire"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetReferenceAcquire",
								majorVersion > 0x37 ? "weakCompareAndSetReferenceAcquire" : "weakCompareAndSetObjectAcquire"
							},
							arr
						);

						// boolean weakCompareAndSetObjectRelease(Object o, long offset, Object expected, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetObjectRelease"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetObjectRelease",
								majorVersion > 0x37 ? "weakCompareAndSetReferenceRelease" : "weakCompareAndSetObjectRelease"
							},
							arr
						);

						// boolean weakCompareAndSetReferenceRelease(Object o, long offset, Object expected, Object x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetReferenceRelease"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetReferenceRelease",
								majorVersion > 0x37 ? "weakCompareAndSetReferenceRelease" : "weakCompareAndSetObjectRelease"
							},
							arr
						);

						// boolean weakCompareAndSetByte(Object o, long offset, byte expected, byte x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							byte.class,
							byte.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetByte"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetByte",
								"weakCompareAndSetByte"
							},
							arr
						);

						// boolean weakCompareAndSetBytePlain(Object o, long offset, byte expected, byte x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetBytePlain"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetBytePlain",
								"weakCompareAndSetBytePlain"
							},
							arr
						);

						// boolean weakCompareAndSetByteAcquire(Object o, long offset, byte expected, byte x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetByteAcquire"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetByteAcquire",
								"weakCompareAndSetByteAcquire"
							},
							arr
						);

						// boolean weakCompareAndSetByteRelease(Object o, long offset, byte expected, byte x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetByteRelease"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetByteRelease",
								"weakCompareAndSetByteRelease"
							},
							arr
						);

						// boolean weakCompareAndSetShort(Object o, long offset, short expected, short x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							short.class,
							short.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetShort"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetShort",
								"weakCompareAndSetShort"
							},
							arr
						);

						// boolean weakCompareAndSetShortPlain(Object o, long offset, short expected, short x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetShortPlain"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetShortPlain",
								"weakCompareAndSetShortPlain"
							},
							arr
						);

						// boolean weakCompareAndSetShortAcquire(Object o, long offset, short expected, short x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetShortAcquire"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetShortAcquire",
								"weakCompareAndSetShortAcquire"
							},
							arr
						);

						// boolean weakCompareAndSetShortRelease(Object o, long offset, short expected, short x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetShortRelease"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetShortRelease",
								"weakCompareAndSetShortRelease"
							},
							arr
						);

						// boolean weakCompareAndSetInt(Object o, long offset, int expected, int x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							int.class,
							int.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetInt"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetInt",
								"weakCompareAndSetInt"
							},
							arr
						);

						// boolean weakCompareAndSetIntPlain(Object o, long offset, int expected, int x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetIntPlain"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetIntPlain",
								"weakCompareAndSetIntPlain"
							},
							arr
						);

						// boolean weakCompareAndSetIntAcquire(Object o, long offset, int expected, int x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetIntAcquire"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetIntAcquire",
								"weakCompareAndSetIntAcquire"
							},
							arr
						);

						// boolean weakCompareAndSetIntRelease(Object o, long offset, int expected, int x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetIntRelease"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetIntRelease",
								"weakCompareAndSetIntRelease"
							},
							arr
						);

						// boolean weakCompareAndSetLong(Object o, long offset, long expected, long x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							long.class,
							long.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetLong"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetLong",
								"weakCompareAndSetLong"
							},
							arr
						);

						// boolean weakCompareAndSetLongPlain(Object o, long offset, long expected, long x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetLongPlain"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetLongPlain",
								"weakCompareAndSetLongPlain"
							},
							arr
						);

						// boolean weakCompareAndSetLongAcquire(Object o, long offset, long expected, long x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetLongAcquire"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetLongAcquire",
								"weakCompareAndSetLongAcquire"
							},
							arr
						);

						// boolean weakCompareAndSetLongRelease(Object o, long offset, long expected, long x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetLongRelease"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetLongRelease",
								"weakCompareAndSetLongRelease"
							},
							arr
						);

						// boolean weakCompareAndSetFloat(Object o, long offset, float expected, float x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							float.class,
							float.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetFloat"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetFloat",
								"weakCompareAndSetFloat"
							},
							arr
						);

						// boolean weakCompareAndSetFloatPlain(Object o, long offset, float expected, float x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetFloatPlain"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetFloatPlain",
								"weakCompareAndSetFloatPlain"
							},
							arr
						);

						// boolean weakCompareAndSetFloatAcquire(Object o, long offset, float expected, float x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetFloatAcquire"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetFloatAcquire",
								"weakCompareAndSetFloatAcquire"
							},
							arr
						);

						// boolean weakCompareAndSetFloatRelease(Object o, long offset, float expected, float x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetFloatRelease"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetFloatRelease",
								"weakCompareAndSetFloatRelease"
							},
							arr
						);

						// boolean weakCompareAndSetDouble(Object o, long offset, double expected, double x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							double.class,
							double.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetDouble"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetDouble",
								"weakCompareAndSetDouble"
							},
							arr
						);

						// boolean weakCompareAndSetDoublePlain(Object o, long offset, double expected, double x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetDoublePlain"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetDoublePlain",
								"weakCompareAndSetDoublePlain"
							},
							arr
						);

						// boolean weakCompareAndSetDoubleAcquire(Object o, long offset, double expected, double x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetDoubleAcquire"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetDoubleAcquire",
								"weakCompareAndSetDoubleAcquire"
							},
							arr
						);

						// boolean weakCompareAndSetDoubleRelease(Object o, long offset, double expected, double x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetDoubleRelease"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetDoubleRelease",
								"weakCompareAndSetDoubleRelease"
							},
							arr
						);

						// boolean weakCompareAndSetBoolean(Object o, long offset, boolean expected, boolean x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							boolean.class,
							boolean.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetBoolean"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetBoolean",
								"weakCompareAndSetBoolean"
							},
							arr
						);

						// boolean weakCompareAndSetBooleanPlain(Object o, long offset, boolean expected, boolean x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetBooleanPlain"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetBooleanPlain",
								"weakCompareAndSetBooleanPlain"
							},
							arr
						);

						// boolean weakCompareAndSetBooleanAcquire(Object o, long offset, boolean expected, boolean x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetBooleanAcquire"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetBooleanAcquire",
								"weakCompareAndSetBooleanAcquire"
							},
							arr
						);

						// boolean weakCompareAndSetBooleanRelease(Object o, long offset, boolean expected, boolean x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetBooleanRelease"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetBooleanRelease",
								"weakCompareAndSetBooleanRelease"
							},
							arr
						);

						// boolean weakCompareAndSetChar(Object o, long offset, char expected, char x);
						arr = new Class[]{
							boolean.class,
							Object.class,
							long.class,
							char.class,
							char.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetChar"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetChar",
								"weakCompareAndSetChar"
							},
							arr
						);

						// boolean weakCompareAndSetCharPlain(Object o, long offset, char expected, char x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetCharPlain"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetCharPlain",
								"weakCompareAndSetCharPlain"
							},
							arr
						);

						// boolean weakCompareAndSetCharAcquire(Object o, long offset, char expected, char x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetCharAcquire"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetCharAcquire",
								"weakCompareAndSetCharAcquire"
							},
							arr
						);

						// boolean weakCompareAndSetCharRelease(Object o, long offset, char expected, char x);
						if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetCharRelease"}, arr);
						else method.accept(
							new String[]{
								"weakCompareAndSetCharRelease",
								"weakCompareAndSetCharRelease"
							},
							arr
						);

						// char getAndAddChar(Object o, long offset, char delta);
						arr = new Class[]{
							char.class,
							Object.class,
							long.class,
							char.class
						};
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndAddChar"}, arr);
						else method.accept(
							new String[]{
								"getAndAddChar",
								"getAndAddChar"
							},
							arr
						);

						// float getAndAddFloat(Object o, long offset, float delta);
						arr[0] = arr[3] = float.class;
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndAddFloat"}, arr);
						else method.accept(
							new String[]{
								"getAndAddFloat",
								"getAndAddFloat"
							},
							arr
						);

						// double getAndAddDouble(Object o, long offset, double delta)
						arr[0] = arr[3] = double.class;
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndAddDouble"}, arr);
						else method.accept(
							new String[]{
								"getAndAddDouble",
								"getAndAddDouble"
							},
							arr
						);

						// boolean getAndSetBoolean(Object o, long offset, boolean newValue);
						arr[0] = arr[3] = boolean.class;
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndSetBoolean"}, arr);
						else method.accept(
							new String[]{
								"getAndSetBoolean",
								"getAndSetBoolean"
							},
							arr
						);

						// char getAndSetChar(Object o, long offset, char newValue);
						arr[0] = arr[3] = char.class;
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndSetChar"}, arr);
						else method.accept(
							new String[]{
								"getAndSetChar",
								"getAndSetChar"
							},
							arr
						);

						// float getAndSetFloat(Object o, long offset, float newValue);
						arr[0] = arr[3] = float.class;
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndSetFloat"}, arr);
						else method.accept(
							new String[]{
								"getAndSetFloat",
								"getAndSetFloat"
							},
							arr
						);

						// double getAndSetDouble(Object o, long offset, double newValue)
						arr[0] = arr[3] = double.class;
						if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndSetDouble"}, arr);
						else method.accept(
							new String[]{
								"getAndSetDouble",
								"getAndSetDouble"
							},
							arr
						);

						v35GetAndAddSetAR.accept("getAndAddByte", byte.class);
						v35GetAndAddSetAR.accept("getAndAddShort", short.class);
						v35GetAndAddSetAR.accept("getAndAddInt", int.class);
						v35GetAndAddSetAR.accept("getAndAddLong", long.class);
						v35GetAndAddSetAR.accept("getAndAddFloat", float.class);
						v35GetAndAddSetAR.accept("getAndAddDouble", double.class);
						v35GetAndAddSetAR.accept("getAndAddChar", char.class);

						arr[0] = arr[3] = Object.class;
						v35method.accept(
							new String[]{
								"getAndSetObjectAcquire",
								majorVersion > 0x37 ? "getAndSetReferenceAcquire" : "getAndSetObjectAcquire"
							},
							arr
						);
						v35method.accept(
							new String[]{
								"getAndSetReferenceAcquire",
								majorVersion > 0x37 ? "getAndSetReferenceAcquire" : "getAndSetObjectAcquire"
							},
							arr
						);
						v35method.accept(
							new String[]{
								"getAndSetObjectRelease",
								majorVersion > 0x37 ? "getAndSetReferenceRelease" : "getAndSetObjectRelease"
							},
							arr
						);
						v35method.accept(
							new String[]{
								"getAndSetReferenceRelease",
								majorVersion > 0x37 ? "getAndSetReferenceRelease" : "getAndSetObjectRelease"
							},
							arr
						);

						v35GetAndAddSetAR.accept("getAndSetByte", byte.class);
						v35GetAndAddSetAR.accept("getAndSetShort", short.class);
						v35GetAndAddSetAR.accept("getAndSetInt", int.class);
						v35GetAndAddSetAR.accept("getAndSetLong", long.class);
						v35GetAndAddSetAR.accept("getAndSetFloat", float.class);
						v35GetAndAddSetAR.accept("getAndSetDouble", double.class);
						v35GetAndAddSetAR.accept("getAndSetBoolean", boolean.class);
						v35GetAndAddSetAR.accept("getAndSetChar", char.class);

						v35GetAndBitwise.accept("Byte", byte.class);
						v35GetAndBitwise.accept("Short", short.class);
						v35GetAndBitwise.accept("Int", int.class);
						v35GetAndBitwise.accept("Long", long.class);
						v35GetAndBitwise.accept("Boolean", boolean.class);
						v35GetAndBitwise.accept("Char", char.class);

						v36method.accept(
							new String[]{
								"objectFieldOffset",
								"objectFieldOffset",
								"(Ljava/lang/Class<*>;Ljava/lang/String;)J"
							},
							new Class[]{
								long.class,
								Class.class,
								String.class
							}
						);
					}

					// static constructor
					{
						MethodVisitor mv = cw.visitMethod(
							AccessFlag.ACC_STATIC,
							"<clinit>",
							"()V",
							null,
							null
						);
						mv.visitCode();
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(ReflectionFactory.class),
							"TRUSTED_LOOKUP",
							getDescriptor(MethodHandles.Lookup.class)
						);
						mv.visitLdcInsn(Type.getType(usfClass));
						mv.visitLdcInsn("theUnsafe");
						mv.visitLdcInsn(Type.getType(usfClass));
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(MethodHandles.Lookup.class),
							"findStaticGetter",
							MethodType.methodType(
								MethodHandle.class,
								Class.class,
								String.class,
								Class.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitTypeInsn(Opcodes.ANEWARRAY, getType(Object.class));
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
						mv.visitTypeInsn(Opcodes.CHECKCAST, getType(usfClass));
						mv.visitFieldInsn(
							Opcodes.PUTSTATIC,
							className,
							"final",
							getDescriptor(usfClass)
						);
						mv.visitInsn(Opcodes.RETURN);
						mv.visitMaxs(4, 0);
						mv.visitEnd();
					}

					byte[] code = cw.toByteArray();
					clazz = (Class<?>) DEFINE.invoke(ReflectionFactory.class.getClassLoader(), null, code, 0, code.length);
				}

				UNSAFE = (Unsafe) usf.allocateInstance(clazz);
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
						"Ljava/lang/Object;Lorg/mve/util/reflect/ReflectionAccessor<Ljava/lang/Class<*>;>;",
						"java/lang/Object",
						new String[]{"org/mve/util/reflect/ReflectionAccessor"}
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
					handleInvoker = (Class<?>) DEFINE.invoke(ReflectionFactory.class.getClassLoader(), null, code, 0, code.length);
				}
//				if (handleInvoker == null) throw new UnknownError();
				METHOD_HANDLE_INVOKER = (ReflectionAccessor<Object>) handleInvoker.getDeclaredConstructor().newInstance();

			}

			/*
			 * ClassLoader class
			 */
			Class<?> internalClassLoader;
			{
				String internal_classloader_name = "org/mve/util/reflect/ClassLoader";
				String superClass = majorVersion > 0x34 ? "jdk/internal/loader/BuiltinClassLoader" : "java/lang/ClassLoader";
				try
				{
					internalClassLoader = Class.forName(internal_classloader_name.replace('/', '.'));
				}
				catch (Throwable t)
				{
					/*
					 * has no constructor
					 */
					ClassWriter cw = new ClassWriter(0);
					cw.visitSource("ClassLoader.java", null);
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
					 * implement method define
					 *   Class<?> define(byte[])
					 */
					{
						MethodVisitor mv = cw.visitMethod(
							AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL | AccessFlag.ACC_SYNTHETIC | AccessFlag.ACC_SYNCHRONIZED,
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
							getType(ReflectionFactory.class),
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
						mv.visitInsn(Opcodes.ARRAYLENGTH);
						warp(int.class, mv);
						mv.visitInsn(Opcodes.AASTORE);
						// invoke method
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
						Label line = new Label();
						mv.visitLabel(line);
						mv.visitLineNumber(10, line);
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
						mv.visitLabel(line);
						mv.visitLineNumber(11, line);


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
						mv.visitTypeInsn(Opcodes.CHECKCAST, getType(Class.class));
						mv.visitInsn(Opcodes.DUP);
						mv.visitJumpInsn(Opcodes.IFNONNULL, ret);


						mv.visitLabel(line);
						mv.visitLineNumber(12, line);
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
						Label tryLabel = new Label();
						mv.visitLabel(tryLabel);
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
						Label catchLabel = new Label();
						mv.visitJumpInsn(Opcodes.GOTO, ret);
						mv.visitFrame(			// FRAME
							Opcodes.F_SAME1,
							0,
							null,
							1,
							new Object[]{"java/lang/Throwable"}
						);
						Label tLabel = new Label();
						mv.visitLabel(tLabel);
						mv.visitLabel(catchLabel);
						mv.visitInsn(Opcodes.POP);
						mv.visitInsn(Opcodes.ACONST_NULL);
						mv.visitFrame(			// FRAME
							Opcodes.F_SAME1,
							0,
							null,
							1,
							new Object[]{"java/lang/Class"}
						);
						mv.visitLabel(ret);
						mv.visitInsn(Opcodes.ARETURN);
						mv.visitTryCatchBlock(tryLabel, catchLabel, tLabel, "java/lang/Throwable");
						mv.visitMaxs(2, flag ? 3 : 2);
						mv.visitEnd();
					}

					byte[] code = cw.toByteArray();
					internalClassLoader = (Class<?>) METHOD_HANDLE_INVOKER.invoke(DEFINE, ReflectionFactory.class.getClassLoader(), null, code, 0, code.length);
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
						"Ljava/lang/Object;Lorg/mve/util/reflect/ReflectionAccessor<Lorg/mve/util/reflect/ReflectionClassLoader;>;",
						"java/lang/Object",
						new String[]{"org/mve/util/reflect/ReflectionAccessor"}
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
						mv.visitLdcInsn("jdk.internal.loader.BuiltinClassLoader");
						mv.visitMethodInsn(
							Opcodes.INVOKESTATIC,
							getType(Class.class),
							"forName",
							MethodType.methodType(
								Class.class,
								String.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitInsn(Opcodes.AALOAD);
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(Class.class),
							"isInstance",
							MethodType.methodType(
								boolean.class,
								Object.class
							).toMethodDescriptorString(),
							false
						);
						Label l1 = new Label();
						mv.visitJumpInsn(Opcodes.IFEQ, l1);
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(ReflectionFactory.class),
							"UNSAFE",
							getDescriptor(Unsafe.class)
						);
						mv.visitLdcInsn(Type.getType(internalClassLoader));
						mv.visitMethodInsn(
							Opcodes.INVOKEINTERFACE,
							getType(Unsafe.class),
							"allocateInstance",
							MethodType.methodType(
								Object.class,
								Class.class
							).toMethodDescriptorString(),
							true
						);
						mv.visitTypeInsn(Opcodes.CHECKCAST, getType(ReflectionClassLoader.class));
						mv.visitVarInsn(Opcodes.ASTORE, 2);

						/*
						 * TRUSTED_LOOKUP.findSetter(clazz, "parent", clazz);
						 */
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(ReflectionFactory.class),
							"TRUSTED_LOOKUP",
							getDescriptor(MethodHandles.Lookup.class)
						);	// lookup
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
						mv.visitInsn(Opcodes.DUP);

						/*
						 * handle.invoke(loader, ((ClassLoader)args[0]).getParent());
						 */
						mv.visitInsn(Opcodes.ICONST_2);
						mv.visitTypeInsn(Opcodes.ANEWARRAY, getType(Object.class));
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitVarInsn(Opcodes.ALOAD, 2);
						mv.visitInsn(Opcodes.AASTORE);
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ICONST_1);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitInsn(Opcodes.AALOAD);
						mv.visitTypeInsn(Opcodes.CHECKCAST, getType(ClassLoader.class));
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(ClassLoader.class),
							"getParent",
							MethodType.methodType(
								ClassLoader.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitInsn(Opcodes.AASTORE);
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
						mv.visitInsn(Opcodes.POP);

						/*
						 * handle.invoke(args[0], loader);
						 */
						Function<MethodVisitor, Void> f = (mv1) ->
						{
							mv1.visitInsn(Opcodes.ICONST_2);
							mv1.visitTypeInsn(Opcodes.ANEWARRAY, getType(Object.class));
							mv1.visitInsn(Opcodes.DUP);
							mv1.visitInsn(Opcodes.ICONST_0);
							mv1.visitVarInsn(Opcodes.ALOAD, 1);
							mv1.visitInsn(Opcodes.ICONST_0);
							mv1.visitInsn(Opcodes.AALOAD);
							mv1.visitInsn(Opcodes.AASTORE);
							mv1.visitInsn(Opcodes.DUP);
							mv1.visitInsn(Opcodes.ICONST_1);
							mv1.visitVarInsn(Opcodes.ALOAD, 2);
							mv1.visitInsn(Opcodes.AASTORE);
							mv1.visitMethodInsn(
								Opcodes.INVOKEVIRTUAL,
								getType(MethodHandle.class),
								"invokeWithArguments",
								MethodType.methodType(
									Object.class,
									Object[].class
								).toMethodDescriptorString(),
								false
							);
							mv1.visitInsn(Opcodes.POP);
							return null;
						};

						f.apply(mv);

						/*
						 * TRUSTED_LOOKUP.findSetter(clazz, "parent", clazz);
						 */
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(ReflectionFactory.class),
							"TRUSTED_LOOKUP",
							getDescriptor(MethodHandles.Lookup.class)
						);	// lookup
						mv.visitLdcInsn("jdk.internal.loader.BuiltinClassLoader");
						mv.visitMethodInsn(
							Opcodes.INVOKESTATIC,
							getType(Class.class),
							"forName",
							MethodType.methodType(
								Class.class,
								String.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitLdcInsn("parent");
						mv.visitLdcInsn("jdk.internal.loader.BuiltinClassLoader");
						mv.visitMethodInsn(
							Opcodes.INVOKESTATIC,
							getType(Class.class),
							"forName",
							MethodType.methodType(
								Class.class,
								String.class
							).toMethodDescriptorString(),
							false
						);
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

						f.apply(mv);

						/*
						 * TRUSTED_LOOKUP.findSetter(internalClassLoader, "this", ClassLoader.class)
						 * 		.invoke(TRUSTED_LOOKUP.findConstructor(clazz, MethodType.methodType(void.class, ClassLoader.class)).invoke(args[0]))
						 */
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(ReflectionFactory.class),
							"TRUSTED_LOOKUP",
							getDescriptor(MethodHandles.Lookup.class)
						);
						mv.visitLdcInsn(Type.getType(internalClassLoader));
						mv.visitLdcInsn("this");
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
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(ReflectionFactory.class),
							"TRUSTED_LOOKUP",
							getDescriptor(MethodHandles.Lookup.class)
						);
						mv.visitLdcInsn(DELEGATING_CLASS.getTypeName());
						mv.visitMethodInsn(
							Opcodes.INVOKESTATIC,
							getType(Class.class),
							"forName",
							MethodType.methodType(
								Class.class,
								String.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(Void.class),
							"TYPE",
							getDescriptor(Class.class)
						);
						mv.visitLdcInsn(Type.getType(ClassLoader.class));
						mv.visitMethodInsn(
							Opcodes.INVOKESTATIC,
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
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitInsn(Opcodes.AALOAD);
						mv.visitInsn(Opcodes.AASTORE);
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
						mv.visitInsn(Opcodes.AASTORE);
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
						mv.visitInsn(Opcodes.POP);

						/*
						 * TRUSTED_LOOKUP.findSetter(internalClassLoader, "class", Map.class)
						 * 		.invoke(loader, new ConcurrentHashMap());
						 */
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(ReflectionFactory.class),
							"TRUSTED_LOOKUP",
							getDescriptor(MethodHandles.Lookup.class)
						);
						mv.visitLdcInsn(Type.getType(internalClassLoader));
						mv.visitLdcInsn("class");
						mv.visitLdcInsn(Type.getType(Map.class));
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
						mv.visitTypeInsn(Opcodes.NEW, getType(ConcurrentHashMap.class));
						mv.visitInsn(Opcodes.DUP);
						mv.visitMethodInsn(
							Opcodes.INVOKESPECIAL,
							getType(ConcurrentHashMap.class),
							"<init>",
							"()V",
							false
						);
						mv.visitInsn(Opcodes.AASTORE);
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
						mv.visitInsn(Opcodes.POP);
						mv.visitVarInsn(Opcodes.ALOAD, 2);

						Label l2 = new Label();
						mv.visitJumpInsn(Opcodes.GOTO, l2);
						mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
						mv.visitLabel(l1);

						/*
						 * standard class loader
						 */
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
						mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"org/mve/util/reflect/ReflectionClassLoader"});
						mv.visitInsn(Opcodes.ARETURN);
						mv.visitMaxs(10, 3);
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
						mv.visitInsn(Opcodes.ARETURN);
						mv.visitMaxs(4, 2);
					}
					mv.visitEnd();
					bridge(cw, "org/mve/util/reflect/ClassLoaderConstructor", ReflectionClassLoader.class);
					cw.visitEnd();
					byte[] code = cw.toByteArray();
					c = (Class<?>) METHOD_HANDLE_INVOKER.invoke(DEFINE, ReflectionFactory.class.getClassLoader(), null, code, 0, code.length);
				}

				CLASS_LOADER_FACTORY = (ReflectionAccessor<ReflectionClassLoader>) c.getDeclaredConstructor().newInstance();
			}

			INTERNAL_CLASS_LOADER = CLASS_LOADER_FACTORY.invoke(ReflectionFactory.class.getClassLoader());

			/*
			 * caller class
			 */
			{
				Class<? extends StackAccessor> c;
				try
				{
					c = (Class<? extends StackAccessor>) Class.forName("org.mve.util.reflect.ThreadStackAccessor");
				}
				catch (Throwable t)
				{
					ClassWriter cw = new ClassWriter(0);
					cw.visit(
						0x34,
						0x21,
						"org/mve/util/reflect/ThreadStackAccessor",
						null,
						getType(SecurityManager.class),
						new String[]{getType(StackAccessor.class)}
					);
					cw.visitSource("ThreadStackAccessor.java", null);

					genericConstructor(cw, getType(SecurityManager.class));

					/*
					 * Class<?> getCallerClass();
					 */
					{
						MethodVisitor mv = cw.visitMethod(
							AccessFlag.ACC_PUBLIC,
							"getCallerClass",
							"()Ljava/lang/Class;",
							"()Ljava/lang/Class<*>;",
							null
						);
						mv.visitCode();
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitMethodInsn(
							Opcodes.INVOKEINTERFACE,
							getType(StackAccessor.class),
							"getStackClassContext",
							"()[Ljava/lang/Class;",
							true
						);
						mv.visitInsn(Opcodes.ICONST_2);
						mv.visitInsn(Opcodes.AALOAD);
						mv.visitInsn(Opcodes.ARETURN);
						mv.visitMaxs(2, 1);
						mv.visitEnd();
					}

					/*
					 * Class<?>[] getStackClassContext();
					 */
					{
						MethodVisitor mv = cw.visitMethod(
							AccessFlag.ACC_PUBLIC,
							"getStackClassContext",
							MethodType.methodType(
								Class[].class
							).toMethodDescriptorString(),
							"()[Ljava/lang/Class<*>;",
							null
						);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(SecurityManager.class),
							"getClassContext",
							MethodType.methodType(
								Class[].class
							).toMethodDescriptorString(),
							false
						);
						mv.visitInsn(Opcodes.DUP);
						mv.visitInsn(Opcodes.ARRAYLENGTH);
						mv.visitInsn(Opcodes.ICONST_1);
						mv.visitInsn(Opcodes.SWAP);
						mv.visitMethodInsn(
							Opcodes.INVOKESTATIC,
							getType(Arrays.class),
							"copyOfRange",
							MethodType.methodType(
								Object[].class,
								Object[].class,
								int.class,
								int.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitTypeInsn(Opcodes.CHECKCAST, "[Ljava/lang/Class;");
						mv.visitInsn(Opcodes.ARETURN);
						mv.visitMaxs(3, 1);
						mv.visitEnd();
					}
					cw.visitEnd();
					byte[] code = cw.toByteArray();
					c = (Class<? extends StackAccessor>) METHOD_HANDLE_INVOKER.invoke(DEFINE, ReflectionFactory.class.getClassLoader(), null, code, 0, code.length);
				}
				STACK_ACCESSOR = c.getDeclaredConstructor().newInstance();
			}

			/*
			 * accessor
			 */
			{
				Class<?> c;
				try
				{
					c = Class.forName("org.mve.util.reflect.MagicAccessor");
				}
				catch (Throwable t)
				{
					ClassWriter cw = new ClassWriter(0);
					cw.visitSource("MagicAccessor.java", null);
					cw.visit(
						0x34,
						0x21,
						"org/mve/util/reflect/MagicAccessor",
						null,
						MAGIC_ACCESSOR,
						new String[]{getType(Accessor.class)}
					);

					genericConstructor(cw, MAGIC_ACCESSOR);

					/*
					 * implement method void setAccessible(AccessibleObject acc, boolean flag);
					 */
					{
						MethodVisitor mv = cw.visitMethod(
							AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL,
							"setAccessible",
							MethodType.methodType(
								void.class,
								AccessibleObject.class,
								boolean.class
							).toMethodDescriptorString(),
							null,
							null
						);
						mv.visitCode();
						Label line = new Label();
						mv.visitLabel(line);
						mv.visitLineNumber(4, line);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitVarInsn(Opcodes.ILOAD, 2);
						mv.visitFieldInsn(
							Opcodes.PUTFIELD,
							getType(AccessibleObject.class),
							"override",
							getDescriptor(boolean.class)
						);
						mv.visitLabel(line);
						mv.visitLineNumber(5, line);
						mv.visitInsn(Opcodes.RETURN);
						mv.visitMaxs(2, 3);
						mv.visitEnd();
					}

					// Class<?> forName(String name);
					{
						MethodVisitor mv = cw.visitMethod(
							AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL,
							"forName",
							MethodType.methodType(
								Class.class,
								String.class
							).toMethodDescriptorString(),
							"(Ljava/lang/String;)Ljava/lang/Class<*>;",
							null
						);
						mv.visitCode();
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitInsn(Opcodes.ICONST_1);
						mv.visitFieldInsn(
							Opcodes.GETSTATIC,
							getType(ReflectionFactory.class),
							"CALLER",
							getDescriptor(ReflectionAccessor.class)
						);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitTypeInsn(Opcodes.ANEWARRAY, getType(Object.class));
						mv.visitMethodInsn(
							Opcodes.INVOKEINTERFACE,
							getType(ReflectionAccessor.class),
							"invoke",
							MethodType.methodType(
								Object.class,
								Object[].class
							).toMethodDescriptorString(),
							true
						);
						mv.visitInsn(Opcodes.DUP);
						mv.visitFieldInsn(
							Opcodes.GETFIELD,
							getType(Class.class),
							"classLoader",
							getDescriptor(ClassLoader.class)
						);
						mv.visitInsn(Opcodes.SWAP);
						mv.visitMethodInsn(
							Opcodes.INVOKESTATIC,
							getType(Class.class),
							"forName0",
							MethodType.methodType(
								Class.class,
								String.class,
								boolean.class,
								ClassLoader.class,
								Class.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitInsn(Opcodes.ARETURN);
						mv.visitMaxs(4, 2);
						mv.visitEnd();
					}

					// Class<?> forName(String name, boolean initialize, ClassLoader loader);
					{
						MethodVisitor mv = cw.visitMethod(
							AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL,
							"forName",
							MethodType.methodType(
								Class.class,
								String.class,
								boolean.class,
								ClassLoader.class
							).toMethodDescriptorString(),
							"(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class<*>;",
							null
						);
						mv.visitCode();
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitVarInsn(Opcodes.ILOAD, 2);
						mv.visitVarInsn(Opcodes.ALOAD, 3);
						mv.visitInsn(Opcodes.ACONST_NULL);
						mv.visitMethodInsn(
							Opcodes.INVOKESTATIC,
							getType(Class.class),
							"forName0",
							MethodType.methodType(
								Class.class,
								String.class,
								boolean.class,
								ClassLoader.class,
								Class.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitInsn(Opcodes.ARETURN);
						mv.visitMaxs(4, 4);
						mv.visitEnd();
					}

					// Class<?> defineClass(ClassLoader loader, byte[] code);
					{
						MethodVisitor mv = cw.visitMethod(
							AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL,
							"defineClass",
							MethodType.methodType(
								Class.class,
								ClassLoader.class,
								byte[].class
							).toMethodDescriptorString(),
							"(Ljava/lang/ClassLoader;[B)Ljava/lang/Class<*>;",
							null
						);
						mv.visitCode();
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitInsn(Opcodes.ACONST_NULL);
						mv.visitVarInsn(Opcodes.ALOAD, 2);
						mv.visitInsn(Opcodes.ICONST_0);
						mv.visitVarInsn(Opcodes.ALOAD, 2);
						mv.visitInsn(Opcodes.ARRAYLENGTH);
						mv.visitMethodInsn(
							Opcodes.INVOKEVIRTUAL,
							getType(ClassLoader.class),
							"defineClass",
							MethodType.methodType(
								Class.class,
								String.class,
								byte[].class,
								int.class,
								int.class
							).toMethodDescriptorString(),
							false
						);
						mv.visitInsn(Opcodes.ARETURN);
						mv.visitMaxs(5, 3);
						mv.visitEnd();
					}

					byte[] code = cw.toByteArray();
					c = INTERNAL_CLASS_LOADER.define(code);
				}
				ACCESSOR = (Accessor) c.getDeclaredConstructor().newInstance();
			}
		}
		catch (Throwable t)
		{
			throw new UninitializedException(t);
		}
	}
}
