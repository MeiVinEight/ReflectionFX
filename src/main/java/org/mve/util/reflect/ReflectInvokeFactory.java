package org.mve.util.reflect;

import org.jetbrains.org.objectweb.asm.ClassWriter;
import org.jetbrains.org.objectweb.asm.FieldVisitor;
import org.jetbrains.org.objectweb.asm.Label;
import org.jetbrains.org.objectweb.asm.MethodVisitor;
import org.jetbrains.org.objectweb.asm.Opcodes;
import org.jetbrains.org.objectweb.asm.Type;
import org.mve.util.IO;
import org.mve.util.asm.OperandStack;
import org.mve.util.asm.file.AccessFlag;
import org.mve.util.asm.file.ClassField;
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
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Objects;

public class ReflectInvokeFactory
{
	private static final MethodHandle DEFINE;
	private static final String SUPER_CLASS;
	private static final Unsafe USF;
	private static final ClassLoader INTERNAL_CLASS_LOADER;
	private static int id = 0;

	public static ReflectInvoker getReflectInvoker(Class<?> clazz, String methodName, Class<?> returnType, Class<?>... params) throws ReflectionGenericException
	{
		try { return checkMethodAndGeneric(clazz.getClassLoader(), clazz, methodName, returnType, params); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker getReflectInvoker(Class<?> clazz, String methodName, boolean isStatic, Class<?> returnType, Class<?>... params) throws ReflectionGenericException
	{
		try { return generic(clazz, methodName, MethodType.methodType(returnType, params), isStatic); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker getReflectInvoker(ClassLoader classLoader, String className, String methodName, Class<?> returnType, Class<?>... params) throws ReflectionGenericException
	{
		try { return checkMethodAndGeneric(classLoader, Class.forName(className, false, classLoader), methodName, returnType, params); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker getReflectInvoker(Class<?> clazz, String fieldName) throws ReflectionGenericException
	{
		try { return checkFieldAndGeneric(clazz.getClassLoader(), clazz, fieldName, false); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker getReflectInvoker(Class<?> clazz, String fieldName, Class<?> type, boolean isStatic, boolean isFinal) throws ReflectionGenericException
	{
		try { return generic(clazz, fieldName, type, isStatic, isFinal, false); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker getReflectInvoker(ClassLoader loader, String className, String fieldName) throws ReflectionGenericException
	{
		try { return checkFieldAndGeneric(loader, Class.forName(className, false, loader), fieldName, false); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker getReflectInvoker(Class<?> clazz, String fieldName, boolean deepReflect) throws ReflectionGenericException
	{
		try { return checkFieldAndGeneric(clazz.getClassLoader(), clazz, fieldName, deepReflect); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker getReflectInvoker(Class<?> clazz, String fieldName, Class<?> type, boolean isStatic, boolean isFinal, boolean deepReflect) throws ReflectionGenericException
	{
		try { return generic(clazz, fieldName, type, isStatic, isFinal, deepReflect); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker getReflectInvoker(ClassLoader loader, String className, String fieldName, boolean deepReflect) throws ReflectionGenericException
	{
		try { return checkFieldAndGeneric(loader, Class.forName(className, false, loader), fieldName, deepReflect); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker getReflectInvoker(Class<?> clazz, Class<?>... params) throws ReflectionGenericException
	{
		try { return checkConstructorAndGeneric(clazz.getClassLoader(), clazz, params); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker getReflectInvoker(ClassLoader loader, String className, Class<?>... params) throws ReflectionGenericException
	{
		try { Class<?> checkClass = Class.forName(className, false, loader);return checkConstructorAndGeneric(loader, checkClass, params); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker getReflectInvoker(Class<?> clazz) throws ReflectionGenericException
	{
		try { return generic(clazz); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker getReflectInvoker(ClassLoader loader, String className) throws ReflectionGenericException
	{
		try { return generic(Class.forName(className, false, loader)); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker throwException() throws ReflectionGenericException
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, null, "java/lang/Object", new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, "java/lang/Object");
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC, "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitInsn(Opcodes.AALOAD);
		mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Throwable");
		mv.visitInsn(Opcodes.ATHROW);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
		try { return define(cw, ClassLoader.getSystemClassLoader()); } catch (Throwable t) { throw new ReflectionGenericException("Can not generic invoker", t); }
	}

	public static ReflectInvoker constant(Object value)
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, null, "java/lang/Object", new String[]{"org/mve/util/reflect/ReflectInvoker"});
		FieldVisitor fv = cw.visitField(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_FINAL, "final", "Ljava/lang/Object;", null, null);
		fv.visitEnd();
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, className, "final", "Ljava/lang/Object;");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
		mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, className, "final", "Ljava/lang/Object;");
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(1, 2);
		mv.visitEnd();
		cw.visitEnd();
		byte[] code = cw.toByteArray();
		try
		{
			Class<?> clazz = (Class<?>) DEFINE.invoke(ReflectInvokeFactory.class.getClassLoader(), null, code, 0, code.length);
			return (ReflectInvoker) clazz.getDeclaredConstructor(Object.class).newInstance(value);
		}
		catch (Throwable throwable)
		{
			throw new ReflectionGenericException("Can not generic invoker", throwable);
		}
	}

	private static ReflectInvoker checkMethodAndGeneric(ClassLoader loader, Class<?> clazz, String methodName, Class<?> returnType, Class<?>... params) throws Throwable
	{
		String descName = clazz.getTypeName().replace('.', '/');
		ClassFile file = findClass(loader, clazz);
		MethodType type = MethodType.methodType(returnType, params);
		ConstantPool pool = file.getConstantPool();
		for (int i = 0; i < file.getMethodCount(); i++)
		{
			ClassMethod method = file.getMethod(i);
			if (
				Objects.requireNonNull(methodName).equals(((ConstantUTF8)pool.getConstantPoolElement(method.getNameIndex())).getUTF8()) &&
					MethodType.fromMethodDescriptorString(
						((ConstantUTF8)pool.getConstantPoolElement(method.getDescriptorIndex())).getUTF8(),
						loader
					).equals(type)) return generic(clazz, methodName, type, (method.getAccessFlag() & AccessFlag.ACC_STATIC) != 0);
		}
		throw new NoSuchMethodException(descName+'.'+methodName+":"+type.toMethodDescriptorString());
	}

	private static ReflectInvoker checkFieldAndGeneric(ClassLoader loader, Class<?> clazz, String fieldName, boolean deepReflect) throws Throwable
	{
		String descName = clazz.getTypeName().replace('.', '/');
		ClassFile file = findClass(loader, clazz);
		ConstantPool pool = file.getConstantPool();
		for (int i = 0; i < file.getFieldCount(); i++)
		{
			ClassField field = file.getField(i);
			if (Objects.requireNonNull(fieldName).equals(((ConstantUTF8)pool.getConstantPoolElement(field.getNameIndex())).getUTF8()))
			{
				Class<?> type = MethodType.fromMethodDescriptorString("()"+((ConstantUTF8)pool.getConstantPoolElement(field.getDescriptorIndex())).getUTF8(), loader).returnType();
				Field f = clazz.getDeclaredField(fieldName);
				return generic(clazz, fieldName, type, (f.getModifiers() & Modifier.STATIC) != 0, (f.getModifiers() & Modifier.FINAL) != 0, deepReflect);
			}
		}
		throw new NoSuchFieldException(descName+"."+fieldName);
	}

	private static ReflectInvoker checkConstructorAndGeneric(ClassLoader loader, Class<?> clazz, Class<?>... params) throws Throwable
	{
		String descName = clazz.getTypeName().replace('.', '/');
		ClassFile file = findClass(loader, clazz);
		MethodType type = MethodType.methodType(void.class, params);
		ConstantPool pool = file.getConstantPool();
		for (int i=0; i<file.getMethodCount(); i++)
		{
			ClassMethod method = file.getMethod(i);
			if (((ConstantUTF8)pool.getConstantPoolElement(method.getNameIndex())).getUTF8().equals("<init>") &&
				MethodType.fromMethodDescriptorString(
					((ConstantUTF8)pool.getConstantPoolElement(method.getDescriptorIndex())).getUTF8(),
					loader
				).equals(type)) return generic(clazz, type);
		}
		throw new NoSuchMethodException(descName+".<init>:"+type.toMethodDescriptorString());
	}

	private static ReflectInvoker generic(Class<?> clazz, String methodName, MethodType type, boolean isStatic) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		String desc = type.toMethodDescriptorString();
		final String owner = clazz.getTypeName().replace('.', '/');
		Class<?> returnType = type.returnType();
		Class<?>[] params = type.parameterArray();
		final OperandStack stack = new OperandStack();

		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, className, null, SUPER_CLASS, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, SUPER_CLASS);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
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
		return define(cw, INTERNAL_CLASS_LOADER);
	}

	private static ReflectInvoker generic(Class<?> clazz, String fieldName, Class<?> type, boolean isStatic, boolean isFinal, boolean deepReflect) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;

		String desc = getDescriptor(type);
		String owner = clazz.getTypeName().replace('.', '/');

		final OperandStack stack = new OperandStack();

		ClassWriter cw = new ClassWriter(0);
		cw.visit(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, null, SUPER_CLASS, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, SUPER_CLASS);
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
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
					long offset = isStatic ? USF.staticFieldOffset(field1) : USF.objectFieldOffset(field1);
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
		return define(cw, INTERNAL_CLASS_LOADER);
	}

	private static ReflectInvoker generic(Class<?> clazz, MethodType type) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		String desc = type.toMethodDescriptorString();
		final OperandStack stack = new OperandStack();
		String owner = clazz.getTypeName().replace('.', '/');
		ClassWriter cw = new ClassWriter(0);
		cw.visit(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER, className, null, SUPER_CLASS, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, SUPER_CLASS);
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
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
		return define(cw, INTERNAL_CLASS_LOADER);
	}

	private static ReflectInvoker generic(Class<?> clazz) throws Throwable
	{
		String className = "org/mve/util/reflect/ReflectInvokerImpl"+id++;
		ClassWriter cw = new ClassWriter(0);
		cw.visit(52, AccessFlag.ACC_STRICT | AccessFlag.ACC_PUBLIC, className, null, SUPER_CLASS, new String[]{"org/mve/util/reflect/ReflectInvoker"});
		genericConstructor(cw, SUPER_CLASS);
		MethodVisitor mv = cw.visitMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitTypeInsn(Opcodes.NEW, clazz.getTypeName().replace('.', '/'));
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(1, 2);
		mv.visitEnd();
		return define(cw, INTERNAL_CLASS_LOADER);
	}

	private static ClassFile findClass(ClassLoader loader, Class<?> clazz) throws IOException
	{
		String descName = clazz.getTypeName().replace('.', '/');
		String pathName = descName.concat(".class");
		URL url = loader.getResource(pathName);
		if (url == null) throw new NullPointerException(pathName);
		InputStream in = url.openStream();
		byte[] code = IO.toByteArray(in);
		in.close();
		return new ClassFile(code);
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

	private static ReflectInvoker define(ClassWriter cw, ClassLoader loader) throws Throwable
	{
		cw.visitEnd();
		byte[] code = cw.toByteArray();
		Class<?> implClass = (Class<?>) DEFINE.invoke(loader, null, code, 0, code.length);
		return (ReflectInvoker) implClass.getDeclaredConstructor().newInstance();
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
				USF = (Unsafe) field.get(null);

				if (majorVersion > 0X34)
				{
					Class<?> clazz = Class.forName("jdk.internal.module.IllegalAccessLogger");
					Field loggerField = clazz.getDeclaredField("logger");
					long offset = USF.staticFieldOffset(loggerField);
					USF.putObjectVolatile(clazz, offset, null);
				}

				if (majorVersion <= 0X34) SUPER_CLASS = "sun/reflect/MagicAccessorImpl";
				else SUPER_CLASS = "jdk/internal/reflect/MagicAccessorImpl";

			}

			Class<?> clazz = Class.forName(majorVersion > 0x34 ? "jdk.internal.reflect.DelegatingClassLoader" : "sun.reflect.DelegatingClassLoader");
			Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
			field.setAccessible(true);
			MethodHandles.Lookup lookup = (MethodHandles.Lookup) field.get(null);
			INTERNAL_CLASS_LOADER = (ClassLoader) lookup.findConstructor(clazz, MethodType.methodType(void.class, ClassLoader.class)).invoke(ClassLoader.getSystemClassLoader());
			DEFINE = lookup.findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class));
//			handle = lookup.findVirtual(Unsafe.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class)).bindTo(usf);
//			url = ReflectInvokeFactory.class.getClassLoader().getResource("org/mve/util/reflect/ReflectInvoker.class");
//			if (url == null) throw new NullPointerException();
//			in = url.openStream();
//			code = IO.toByteArray(in);
//			handle.invoke(null, code, 0, code.length, null, null);
		}
		catch (Throwable t)
		{
			throw new UninitializedException(t);
		}
	}
}
