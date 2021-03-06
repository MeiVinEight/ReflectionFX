package org.mve.invoke.common;

import org.mve.asm.AnnotationWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.attribute.RuntimeVisibleAnnotationsWriter;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;

import java.lang.invoke.MethodType;

public abstract class Generator
{
	public static final String[] CONSTANT_POOL = new String[4];

	public static boolean isVMAnonymousClass(Class<?> cls)
	{
		return cls.getName().contains("/");
	}

	public static void inline(MethodWriter mw)
	{
		mw.attribute(new RuntimeVisibleAnnotationsWriter()
			.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[1]))
			.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[2]))
			.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))

		);
	}

	public static String getType(Class<?> clazz)
	{
		if (clazz.isArray())
		{
			return getSignature(clazz);
		}
		else
		{
			return clazz.getTypeName().replace('.', '/');
		}
	}

	public static String getSignature(Class<?> clazz)
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
		if (type == byte.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putByteVolatile", "(Ljava/lang/Object;JB)V", true);
		else if (type == short.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putShortVolatile", "(Ljava/lang/Object;JS)V", true);
		else if (type == int.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putIntVolatile", "(Ljava/lang/Object;JI)V", true);
		else if (type == long.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putLongVolatile", "(Ljava/lang/Object;JJ)V", true);
		else if (type == float.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putFloatVolatile", "(Ljava/lang/Object;JF)V", true);
		else if (type == double.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putDoubleVolatile", "(Ljava/lang/Object;JD)V", true);
		else if (type == boolean.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putBooleanVolatile", "(Ljava/lang/Object;JZ)V", true);
		else if (type == char.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putCharVolatile", "(Ljava/lang/Object;JC)V", true);
		else code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true);
	}

	public static void unsafeget(Class<?> type, CodeWriter code)
	{
		if (type == byte.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "getByteVolatile", "(Ljava/lang/Object;J)B", true);
		else if (type == short.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "getShortVolatile", "(Ljava/lang/Object;J)S", true);
		else if (type == int.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "getIntVolatile", "(Ljava/lang/Object;J)I", true);
		else if (type == long.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "getLongVolatile", "(Ljava/lang/Object;J)J", true);
		else if (type == float.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "getFloatVolatile", "(Ljava/lang/Object;J)F", true);
		else if (type == double.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "getDoubleVolatile", "(Ljava/lang/Object;J)D", true);
		else if (type == boolean.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "getBooleanVolatile", "(Ljava/lang/Object;J)Z", true);
		else if (type == char.class) code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "getCharVolatile", "(Ljava/lang/Object;J)C", true);
		else code.method(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "getObjectVolatile", "(Ljava/lang/Object;J)Ljava/lang/Object;", true);
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

	static
	{
		Unsafe unsafe = ReflectionFactory.UNSAFE;
		CONSTANT_POOL[0] = "java/lang/MagicAccessorFactory";
		CONSTANT_POOL[1] = unsafe.getJavaVMVersion() < 57 ? "Ljava/lang/invoke/LambdaForm$Hidden;" : "Ljdk/internal/vm/annotation/Hidden;";
		CONSTANT_POOL[2] = unsafe.getJavaVMVersion() == 0x34 ? "Ljava/lang/invoke/ForceInline;" : "Ljdk/internal/vm/annotation/ForceInline;";
		CONSTANT_POOL[3] = "Ljava/lang/invoke/LambdaForm$Compiled;";
	}
}
