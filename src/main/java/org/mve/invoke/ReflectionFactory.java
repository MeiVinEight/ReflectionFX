package org.mve.invoke;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.attribute.SourceWriter;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.JavaVM;
import org.mve.invoke.common.MagicAccessorBuilder;
import org.mve.invoke.common.standard.AllocatorGenerator;
import org.mve.invoke.common.standard.MagicAllocatorGenerator;
import org.mve.invoke.common.standard.NativeAllocatorGenerator;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unchecked", "unused"})
public class ReflectionFactory
{
	/**
	 * A wrapper of Unsafe
	 * Includes Unsafe for Java 8 and above
	 */
	public static final Unsafe UNSAFE = Unsafe.unsafe;

	/**
	 * The root lookup in jdk
	 * It can find any instruction regardless of access rights
	 */
	public static final MethodHandles.Lookup TRUSTED_LOOKUP = Unsafe.TRUSTED_LOOKUP;

	public static final MagicAccessor ACCESSOR;
	public static final int
		KIND_INVOKE_VIRTUAL		= 0,
		KIND_INVOKE_SPECIAL		= 1,
		KIND_INVOKE_STATIC		= 2,
		KIND_INVOKE_INTERFACE	= 3,
		KIND_GET				= 4,
		KIND_PUT				= 5;

	private static final Map<Field, FieldAccessor<?>> GENERATED_FIELD_ACCESSOR = new ConcurrentHashMap<>();
	private static final Map<Method, MethodAccessor<?>> GENERATED_METHOD_ACCESSOR = new ConcurrentHashMap<>();
	private static final Map<Constructor<?>, ConstructorAccessor<?>> GENERATED_CONSTRUCTOR_ACCESSOR = new ConcurrentHashMap<>();
	private static final Map<Class<?>, ReflectionAccessor<?>> GENERATED_ALLOCATOR = new ConcurrentHashMap<>();
	private static final Map<Class<?>, EnumHelper<?>> GENERATED_ENUM_HELPER = new ConcurrentHashMap<>();

	public static <T> MethodAccessor<T> access(Class<?> target, String name, MethodType type, int kind)
	{
		return generic(ACCESSOR.getMethod(target, name, type.returnType(), type.parameterArray()), kind);
	}

	public static <T> MethodAccessor<T> access(Method method, int kind)
	{
		return generic(method, kind);
	}

	public static <T> FieldAccessor<T> access(Class<?> target, String name)
	{
		return generic(ACCESSOR.getField(target, name));
	}

	public static <T> FieldAccessor<T> access(Field field)
	{
		return generic(field);
	}

	public static <T> ConstructorAccessor<T> access(Class<?> target, MethodType type)
	{
		return generic(ACCESSOR.getConstructor(target, type.parameterArray()));
	}

	public static <T> ConstructorAccessor<T> access(Constructor<?> constructor)
	{
		return generic(constructor);
	}

	public static <T> ReflectionAccessor<T> access(Class<?> target)
	{
		return generic(target);
	}

	public static ReflectionAccessor<Void> throwException()
	{
		String className = "org/mve/invoke/Thrower";
		ClassWriter cw = new ClassWriter()
			.set(0x34, 0x21, className, "java/lang/Object", Generator.type(ReflectionAccessor.class))
			.attribute(new SourceWriter("Thrower.java"))
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;")
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ICONST_0)
					.instruction(Opcodes.AALOAD)
					.type(Opcodes.CHECKCAST, "java/lang/Throwable")
					.instruction(Opcodes.ATHROW)
					.max(2, 2)
				)
			);
		return (ReflectionAccessor<Void>) UNSAFE.allocateInstance(Generator.defineAnonymous(ReflectionFactory.class, cw.toByteArray()));
	}

	public static <T> ReflectionAccessor<T> constant(T value)
	{
		String className = "org/mve/invoke/ConstantValue";
		ClassWriter cw = new ClassWriter()
			.set(0x34, AccessFlag.PUBLIC | AccessFlag.FINAL | AccessFlag.SUPER, className, "java/lang/Object", Generator.type(ReflectionAccessor.class))
			.attribute(new SourceWriter("ConstantValue.java"))
			.field(new FieldWriter()
				.set(AccessFlag.PRIVATE | AccessFlag.FINAL, "0", "Ljava/lang/Object;")
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "<init>", "(Ljava/lang/Object;)V")
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.instruction(Opcodes.DUP)
					.method(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
					.instruction(Opcodes.ALOAD_1)
					.field(Opcodes.PUTFIELD, className, "0", "Ljava/lang/Object;")
					.instruction(Opcodes.RETURN)
					.max(2, 2)
				)
			)
			.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "invoke", "()Ljava/lang/Object;")
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.field(Opcodes.GETFIELD, className, "0", "Ljava/lang/Object;")
					.instruction(Opcodes.ARETURN)
					.max(1, 2)
				)
			);
		return ACCESSOR.construct(Generator.defineAnonymous(ReflectionFactory.class, cw.toByteArray()), new Class[]{Object.class}, new Object[]{value});
	}

	public static <T> EnumHelper<T> enumHelper(Class<?> target)
	{
		return (EnumHelper<T>) GENERATED_ENUM_HELPER.computeIfAbsent(target, (k) -> new PolymorphismFactory<>(EnumHelper.class).enumHelper(k).allocate());
	}

	private static <T> MethodAccessor<T> generic(Method target, int kind)
	{
		MethodAccessor<T> generated = (MethodAccessor<T>) GENERATED_METHOD_ACCESSOR.get(target);
		if (generated != null)
		{
			return generated;
		}

		generated = Generator.generate(target, kind, new Object[0]);
		GENERATED_METHOD_ACCESSOR.put(target, generated);
		return generated;
	}

	private static <T> FieldAccessor<T> generic(Field target)
	{
		UNSAFE.ensureClassInitialized(target.getDeclaringClass());
		FieldAccessor<T> generated = (FieldAccessor<T>) GENERATED_FIELD_ACCESSOR.get(target);
		if (generated != null)
		{
			return generated;
		}
		generated = Generator.generate(target, new Object[0]);
		GENERATED_FIELD_ACCESSOR.put(target, generated);
		return generated;
	}

	private static <T> ConstructorAccessor<T> generic(Constructor<?> target)
	{
		ConstructorAccessor<T> generated = (ConstructorAccessor<T>) GENERATED_CONSTRUCTOR_ACCESSOR.get(target);
		if (generated != null)
		{
			return generated;
		}

		generated = Generator.generate(target, new Object[0]);
		GENERATED_CONSTRUCTOR_ACCESSOR.put(target, generated);
		return generated;
	}

	private static <T> ReflectionAccessor<T> generic(Class<?> target)
	{
		ReflectionAccessor<T> generated = (ReflectionAccessor<T>) GENERATED_ALLOCATOR.get(target);
		if (generated != null)
		{
			return generated;
		}
		if (Generator.typeWarp(target) == Void.class || target.isPrimitive() || target.isArray()) throw new IllegalArgumentException("illegal type: "+target);
		boolean access = Generator.checkAccessible(target.getClassLoader());
		
		AllocatorGenerator generator;
		if (Generator.anonymous(target))
		{
			generator = new NativeAllocatorGenerator(target, new Object[0]);
		}
		else
		{
			generator = new MagicAllocatorGenerator(target, new Object[0]);
		}
		generator.generate();

		ClassWriter bytecode = generator.bytecode();
		byte[] code = bytecode.toByteArray();
		Class<?> c = UNSAFE.defineClass(null, code, 0, code.length, ReflectionFactory.class.getClassLoader(), null);
		generator.postgenerate(c);

		generated = (ReflectionAccessor<T>) UNSAFE.allocateInstance(c);
		GENERATED_ALLOCATOR.put(target, generated);
		return generated;
	}

	static
	{
		try
		{
			/*
			 * accessor
			 */
			{
				Class<?> c;
				try
				{
					c = Class.forName(MagicAccessorBuilder.CLASS_NAME.replace('/', '.'));
				}
				catch (Throwable t)
				{
					MagicAccessorBuilder builder = new MagicAccessorBuilder();
					byte[] code = builder.build(UNSAFE);
					builder.prebuild(UNSAFE);
					c = UNSAFE.defineClass(null, code, 0, code.length, ReflectionFactory.class.getClassLoader(), null);
					builder.postbuild(UNSAFE, c);
				}
				ACCESSOR = (MagicAccessor) UNSAFE.allocateInstance(c);
			}
		}
		catch (Throwable t)
		{
			JavaVM.exception(t);
			throw new RuntimeException();
		}
	}
}
