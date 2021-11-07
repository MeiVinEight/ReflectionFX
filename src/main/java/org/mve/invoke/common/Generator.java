package org.mve.invoke.common;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.attribute.RuntimeVisibleAnnotationWriter;
import org.mve.asm.attribute.annotation.Annotation;
import org.mve.invoke.ConstructorAccessor;
import org.mve.invoke.FieldAccessor;
import org.mve.invoke.MethodAccessor;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;
import org.mve.invoke.common.standard.ConstructorAccessorGenerator;
import org.mve.invoke.common.standard.FieldAccessorGenerator;
import org.mve.invoke.common.standard.MagicConstructorAccessorGenerator;
import org.mve.invoke.common.standard.MagicMethodAccessorGenerator;
import org.mve.invoke.common.standard.MethodAccessorGenerator;
import org.mve.invoke.common.standard.NativeConstructorAccessorGenerator;
import org.mve.invoke.common.standard.NativeMethodAccessorGenerator;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.UUID;

public abstract class Generator
{
	private static final SecureRandom R = new SecureRandom();
	public static Unsafe UNSAFE = ReflectionFactory.UNSAFE;

	public static String name()
	{
		byte[] random = new byte[16];
		R.nextBytes(random);
		random[0] |= 0xA0;
		long m = 0;
		long l = 0;
		for (int i=0; i<8; i++)
		{
			m = (m << 8) | (random[i] & 0xff);
		}
		for (int i=8; i<16; i++)
		{
			l = (l << 8) | (random[i] & 0xff);
		}
		return new UUID(m, l).toString().toUpperCase().replaceAll("-", "");
	}

	public static boolean anonymous(Class<?> cls)
	{
		return cls.getName().contains("/");
	}

	public static Class<?> defineAnonymous(Class<?> host, byte[] code)
	{
		return UNSAFE.defineAnonymousClass(host, code, null);
	}

	public static void inline(MethodWriter mw)
	{
		mw.attribute(new RuntimeVisibleAnnotationWriter()
			.annotation(new Annotation().type(JavaVM.CONSTANT[1]))
			.annotation(new Annotation().type(JavaVM.CONSTANT[2]))
			.annotation(new Annotation().type(JavaVM.CONSTANT[3]))
		);
	}

	public static String type(Class<?> clazz)
	{
		if (clazz.isArray())
		{
			return signature(clazz);
		}
		else
		{
			return clazz.getTypeName().replace('.', '/');
		}
	}

	public static String signature(Class<?> clazz)
	{
		return MethodType.methodType(clazz).toMethodDescriptorString().substring(2);
	}

	public static void warp(Class<?> c, CodeWriter code)
	{
		if (c == byte.class) code.method(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
		else if (c == short.class) code.method(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
		else if (c == int.class) code.method(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		else if (c == long.class) code.method(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
		else if (c == float.class) code.method(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
		else if (c == double.class) code.method(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
		else if (c == boolean.class) code.method(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
		else if (c == char.class) code.method(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
	}

	public static void unwarp(Class<?> c, CodeWriter code)
	{
		if (c == byte.class) code.method(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "byteValue", "()B", false);
		else if (c == short.class) code.method(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "shortValue", "()S", false);
		else if (c == int.class) code.method(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
		else if (c == long.class) code.method(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J", false);
		else if (c == float.class) code.method(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", false);
		else if (c == double.class) code.method(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
		else if (c == boolean.class) code.method(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
		else if (c == char.class) code.method(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
	}

	public static Class<?> typeWarp(Class<?> type)
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

	public static void returner(Class<?> type, CodeWriter code)
	{
		if (type == void.class)
		{
			code.instruction(Opcodes.RETURN);
		}
		else if (integer(type))
		{
			code.instruction(Opcodes.IRETURN);
		}
		else if (type == long.class)
		{
			code.instruction(Opcodes.LRETURN);
		}
		else if (type == float.class)
		{
			code.instruction(Opcodes.FRETURN);
		}
		else if (type == double.class)
		{
			code.instruction(Opcodes.DRETURN);
		}
		else
		{
			code.instruction(Opcodes.ARETURN);
		}
	}

	public static void load(Class<?> type, CodeWriter code, int slot)
	{
		if (integer(type))
		{
			code.variable(Opcodes.ILOAD, slot);
		}
		else if (long.class == type)
		{
			code.variable(Opcodes.LLOAD, slot);
		}
		else if (float.class == type)
		{
			code.variable(Opcodes.FLOAD, slot);
		}
		else if (double.class == type)
		{
			code.variable(Opcodes.DLOAD, slot);
		}
		else
		{
			code.variable(Opcodes.ALOAD, slot);
		}
	}

	public static void store(Class<?> type, CodeWriter code, int slot)
	{
		if (integer(type))
		{
			code.variable(Opcodes.ISTORE, slot);
		}
		else if (long.class == type)
		{
			code.variable(Opcodes.LSTORE, slot);
		}
		else if (float.class == type)
		{
			code.variable(Opcodes.FSTORE, slot);
		}
		else if (double.class == type)
		{
			code.variable(Opcodes.DSTORE, slot);
		}
		else
		{
			code.variable(Opcodes.ASTORE, slot);
		}
	}

	public static int typeSize(Class<?> c)
	{
		if (c == void.class) return 0;
		else if (c == long.class || c == double.class) return 2;
		else return 1;
	}

	public static void unsafeput(Class<?> type, CodeWriter code)
	{
		if (type == byte.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "putByte", "(Ljava/lang/Object;JB)V", true);
		else if (type == short.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "putShort", "(Ljava/lang/Object;JS)V", true);
		else if (type == int.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "putInt", "(Ljava/lang/Object;JI)V", true);
		else if (type == long.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "putLong", "(Ljava/lang/Object;JJ)V", true);
		else if (type == float.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "putFloat", "(Ljava/lang/Object;JF)V", true);
		else if (type == double.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "putDouble", "(Ljava/lang/Object;JD)V", true);
		else if (type == boolean.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "putBoolean", "(Ljava/lang/Object;JZ)V", true);
		else if (type == char.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "putChar", "(Ljava/lang/Object;JC)V", true);
		else code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "putObject", "(Ljava/lang/Object;JLjava/lang/Object;)V", true);
	}

	public static void unsafeget(Class<?> type, CodeWriter code)
	{
		if (type == byte.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "getByte", "(Ljava/lang/Object;J)B", true);
		else if (type == short.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "getShort", "(Ljava/lang/Object;J)S", true);
		else if (type == int.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "getInt", "(Ljava/lang/Object;J)I", true);
		else if (type == long.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "getLong", "(Ljava/lang/Object;J)J", true);
		else if (type == float.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "getFloat", "(Ljava/lang/Object;J)F", true);
		else if (type == double.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "getDouble", "(Ljava/lang/Object;J)D", true);
		else if (type == boolean.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "getBoolean", "(Ljava/lang/Object;J)Z", true);
		else if (type == char.class) code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "getChar", "(Ljava/lang/Object;J)C", true);
		else code.method(Opcodes.INVOKEINTERFACE, type(Unsafe.class), "getObject", "(Ljava/lang/Object;J)Ljava/lang/Object;", true);
	}

	public static boolean integer(Class<?> type)
	{
		return type == byte.class || type == short.class || type == int.class || type == boolean.class || type == char.class;
	}

	public static int parameterSize(Class<?>[] params)
	{
		int size = 0;
		for (Class<?> c : params) size += Generator.typeSize(c);
		return size;
	}

	public static boolean checkAccessible(ClassLoader loader)
	{
		return checkAccessible(loader, ReflectionFactory.class.getClassLoader());
	}

	public static boolean checkAccessible(ClassLoader c1, ClassLoader c2)
	{
		while (true)
		{
			if (c1 == c2) return true;
			if (c1 == null) break;
			c1 = c1.getParent();
		}
		return false;
	}

	public static String kind(int kind)
	{
		switch (kind)
		{
			case ReflectionFactory.KIND_INVOKE_VIRTUAL:
			{
				return "VIRTUAL";
			}
			case ReflectionFactory.KIND_INVOKE_SPECIAL:
			{
				return "SPECIAL";
			}
			case ReflectionFactory.KIND_INVOKE_STATIC:
			{
				return "STATIC";
			}
			case ReflectionFactory.KIND_INVOKE_INTERFACE:
			{
				return "INTERFACE";
			}
			case ReflectionFactory.KIND_GET:
			{
				return "GET";
			}
			case ReflectionFactory.KIND_PUT:
			{
				return "PUT";
			}
			default:
			{
				return null;
			}
		}
	}

	public static void merge(CodeWriter code, String name, int argument)
	{
		code.number(Opcodes.SIPUSH, argument)
			.instruction(Opcodes.ALOAD_1)
			.instruction(Opcodes.ARRAYLENGTH)
			.instruction(Opcodes.IADD)
			.type(Opcodes.ANEWARRAY, Generator.type(Object.class))
			.instruction(Opcodes.ASTORE_2)
			.field(Opcodes.GETSTATIC, name, JavaVM.CONSTANT[6], Generator.signature(Object[].class))
			.instruction(Opcodes.ICONST_0)
			.instruction(Opcodes.ALOAD_2)
			.instruction(Opcodes.ICONST_0)
			.number(Opcodes.SIPUSH, argument)
			.method(Opcodes.INVOKESTATIC, Generator.type(System.class), "arraycopy", MethodType.methodType(void.class, Object.class, int.class, Object.class, int.class, int.class).toMethodDescriptorString(), false)
			.instruction(Opcodes.ALOAD_1)
			.instruction(Opcodes.ICONST_0)
			.instruction(Opcodes.ALOAD_2)
			.number(Opcodes.SIPUSH, argument)
			.instruction(Opcodes.ALOAD_1)
			.instruction(Opcodes.ARRAYLENGTH)
			.method(Opcodes.INVOKESTATIC, Generator.type(System.class), "arraycopy", MethodType.methodType(void.class, Object.class, int.class, Object.class, int.class, int.class).toMethodDescriptorString(), false)
			.instruction(Opcodes.ALOAD_2)
			.instruction(Opcodes.ASTORE_1);
	}

	public static void with(ClassWriter bytecode, Class<?> type)
	{
		bytecode.method(new MethodWriter()
			.set(AccessFlag.PUBLIC, ReflectionAccessor.WITH, MethodType.methodType(ReflectionAccessor.class, Object[].class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.instruction(Opcodes.ALOAD_0)
				.instruction(Opcodes.ALOAD_1)
				.method(Opcodes.INVOKEVIRTUAL, bytecode.name, ReflectionAccessor.WITH, MethodType.methodType(type, Object[].class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ARETURN)
				.max(2, 2)
			)
		);
	}

	@SuppressWarnings("unchecked")
	public static <T> MethodAccessor<T> generate(Method method, int kind, Object[] argument)
	{
		MethodAccessorGenerator generator;
		if (Generator.anonymous(method.getDeclaringClass()))
		{
			generator = new NativeMethodAccessorGenerator(method, kind, argument);
		}
		else
		{
			generator = new MagicMethodAccessorGenerator(method, kind, argument);
		}
		generator.generate();

		ClassWriter bytecode = generator.bytecode();
		Class<?> clazz = method.getDeclaringClass();
		boolean access = Generator.checkAccessible(clazz.getClassLoader());
		Class<?> c = defineAnonymous(access ? clazz : ReflectionFactory.class, bytecode.toByteArray());
		generator.postgenerate(c);

		return (MethodAccessor<T>) UNSAFE.allocateInstance(c);
	}

	@SuppressWarnings("unchecked")
	public static <T> FieldAccessor<T> generate(Field field, Object[] argument)
	{
		Class<?> clazz = field.getDeclaringClass();
		boolean acc = Generator.checkAccessible(clazz.getClassLoader());

		FieldAccessorGenerator generator = new FieldAccessorGenerator(field, argument);
		generator.generate();

		ClassWriter bytecode = generator.bytecode();
		Class<?> c = defineAnonymous(acc ? clazz : ReflectionFactory.class, bytecode.toByteArray());
		generator.postgenerate(c);
		return  (FieldAccessor<T>) UNSAFE.allocateInstance(c);
	}

	@SuppressWarnings("unchecked")
	public static <T> ConstructorAccessor<T> generate(Constructor<?> constructor, Object[] argument)
	{
		Class<?> clazz = constructor.getDeclaringClass();
		boolean access = Generator.checkAccessible(clazz.getClassLoader());

		ConstructorAccessorGenerator generator;
		if (Generator.anonymous(clazz))
		{
			generator = new NativeConstructorAccessorGenerator(constructor, argument);
		}
		else
		{
			generator = new MagicConstructorAccessorGenerator(constructor, argument);
		}
		generator.generate();

		ClassWriter bytecode = generator.bytecode();
		Class<?> c = defineAnonymous(access ? clazz : ReflectionFactory.class, bytecode.toByteArray());
		generator.postgenerate(c);
		return  (ConstructorAccessor<T>) UNSAFE.allocateInstance(c);
	}
}
