package org.mve;

import org.jetbrains.org.objectweb.asm.ClassWriter;
import org.jetbrains.org.objectweb.asm.FieldVisitor;
import org.jetbrains.org.objectweb.asm.Label;
import org.jetbrains.org.objectweb.asm.MethodVisitor;
import org.jetbrains.org.objectweb.asm.Opcodes;
import org.jetbrains.org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main
{
	private static MethodHandles.Lookup TRUSTED_LOOKUP;
	private static MethodHandle DEFINE;
	public static void main(String[] args) throws Throwable
	{
		Field f = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
		f.setAccessible(true);
		MethodHandles.Lookup trusted = TRUSTED_LOOKUP = (MethodHandles.Lookup) f.get(null);
		{
			MethodHandle jla_handle = trusted.findStaticGetter(
				Class.forName(new String("jdk.internal.misc.SharedSecrets".getBytes())),
				"javaLangAccess",
				Class.forName(new String("jdk.internal.misc.JavaLangAccess".getBytes()))
			);
			Object jla = jla_handle.invoke();
			MethodHandle exports = trusted.findVirtual(
				Class.forName(new String("jdk.internal.misc.JavaLangAccess".getBytes())),
				"addExportsToAllUnnamed",
				MethodType.methodType(
					void.class,
					Class.forName("java.lang.Module"),
					String.class
				)
			);
			exports.invoke(
				jla,
				Class.class.getMethod("getModule").invoke(Object.class),
				"jdk.internal.loader"
			);
		}
		String internal_classloader_name = "jdk/internal/loader/ClassLoader";
		String superClass = "jdk/internal/loader/BuiltinClassLoader";
		ClassWriter cw = new ClassWriter(0);
		cw.visit(
			0x34,
			Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
			internal_classloader_name,
			null,
			superClass,
			null
		);
		/*
		 * fields
		 */
		{
			/*
			 * private final Map<String, Class<?>> class
			 */
			FieldVisitor fv = cw.visitField(
				Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_FINAL,
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
				Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PRIVATE,
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
				Opcodes.ACC_PUBLIC,
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
			mv.visitLdcInsn(UUID.randomUUID().toString());
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
			mv.visitTypeInsn(Opcodes.CHECKCAST, superClass);
			mv.visitInsn(Opcodes.ACONST_NULL);
			mv.visitMethodInsn(
				Opcodes.INVOKESPECIAL,
				superClass,
				"<init>",
				MethodType.methodType(
					void.class,
					String.class,
					Class.forName(superClass.replace('/', '.')),
					Class.forName(new String("jdk.internal.loader.URLClassPath".getBytes()))
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
				getType(Main.class),
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
				getType(Main.class),
				"TRUSTED_LOOKUP",
				getDescriptor(MethodHandles.Lookup.class)
			);
			mv.visitFieldInsn(
				Opcodes.GETSTATIC,
				getType(Main.class),
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
				MethodHandles.Lookup.class.getTypeName().replace('.', '/'),
				"findConstructor",
				MethodType.methodType(
					MethodHandle.class,
					Class.class,
					MethodType.class
				).toMethodDescriptorString(),
				false
			);
			mv.visitInsn(Opcodes.ICONST_1);
			mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
			mv.visitInsn(Opcodes.DUP);
			mv.visitInsn(Opcodes.ICONST_0);
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			mv.visitInsn(Opcodes.AASTORE);
			mv.visitMethodInsn(
				Opcodes.INVOKEVIRTUAL,
				MethodHandle.class.getTypeName().replace('.', '/'),
				"invoke",
				MethodType.methodType(
					Object.class,
					Object[].class
				).toMethodDescriptorString(),
				false
			);
			mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/ClassLoader");
			mv.visitFieldInsn(
				Opcodes.PUTFIELD,
				internal_classloader_name,
				"this",
				"Ljava/lang/ClassLoader;"
			);
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(6, 3);
			mv.visitEnd();
		}

		{
			String methodName = "loadClassOrNull";
			String desc = "(Ljava/lang/String;Z)Ljava/lang/Class;";
			String signature = "(Ljava/lang/String;Z)Ljava/lang/Class<*>;";
			MethodVisitor mv = cw.visitMethod(
				Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
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
			mv.visitMaxs(2, 3);
		}

		byte[] code = cw.toByteArray();
		DEFINE = trusted.findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class));
		Class<?> c = (Class<?>) DEFINE.invoke(ClassLoader.getSystemClassLoader(), null, code, 0, code.length);
		ClassLoader cl = (ClassLoader) c.getDeclaredConstructor(ClassLoader.class).newInstance(ClassLoader.getSystemClassLoader());
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
}
