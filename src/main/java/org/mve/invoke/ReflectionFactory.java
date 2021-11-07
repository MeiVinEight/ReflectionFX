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
import org.mve.invoke.common.UnsafeBuilder;
import org.mve.invoke.common.standard.AllocatorGenerator;
import org.mve.invoke.common.standard.MagicAllocatorGenerator;
import org.mve.invoke.common.standard.NativeAllocatorGenerator;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unchecked", "unused"})
public class ReflectionFactory
{
	/**
	 * A wrapper of Unsafe
	 * Includes Unsafe for Java 8 and above
	 */
	public static final Unsafe UNSAFE;

	/**
	 * The root lookup in jdk
	 * It can find any instruction regardless of access rights
	 */
	public static final MethodHandles.Lookup TRUSTED_LOOKUP;

	/**
	 * Call MethodHandle without any exception thrown
	 */
	public static final ReflectionAccessor<Object> METHOD_HANDLE_INVOKER;

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
			.set(0x34, 0x21, className, "java/lang/Object", new String[]{Generator.type(ReflectionAccessor.class)})
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
			.set(0x34, AccessFlag.PUBLIC | AccessFlag.FINAL | AccessFlag.SUPER, className, "java/lang/Object", new String[]{Generator.type(ReflectionAccessor.class)})
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
		Class<?> c = Generator.defineAnonymous(access ? target : ReflectionFactory.class, bytecode.toByteArray());
		generator.postgenerate(c);
		generated = (ReflectionAccessor<T>) UNSAFE.allocateInstance(c);
		GENERATED_ALLOCATOR.put(target, generated);
		return generated;
	}

	static
	{
		try
		{
			boolean openJ9VM = JavaVM.VENDOR.equals("Eclipse OpenJ9");

			/*
			 * MagicAccessorImpl
			 */
			String mai;
			{
				if (JavaVM.VERSION <= 0X34) mai = "sun/reflect/MagicAccessorImpl";
				else mai = "jdk/internal/reflect/MagicAccessorImpl";
			}

			final MethodHandle DEFINE;

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
				MethodHandles.lookup();
				Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
				long off = usf.staticFieldOffset(field);
				MethodHandles.Lookup lookup = TRUSTED_LOOKUP = (MethodHandles.Lookup) usf.getObjectVolatile(MethodHandles.Lookup.class, off);
				if (openJ9VM)
				{
					@SuppressWarnings("all")
					Field accClass = MethodHandles.Lookup.class.getDeclaredField("accessClass");
					usf.putObject(TRUSTED_LOOKUP, usf.objectFieldOffset(accClass), Class.forName(mai.replace('/', '.')));
				}
				DEFINE = lookup.findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class));
			}

			/*
			 * MagicAccessFactory
			 */
			{
				try
				{
					Class.forName(JavaVM.CONSTANT[0].replace('/', '.'));
				}
				catch (Throwable t)
				{
					Class<?> usfClass = Class.forName(JavaVM.VERSION > 0x34 ? "jdk.internal.misc.Unsafe" : "sun.misc.Unsafe");
					MethodHandle usfDefineClass = TRUSTED_LOOKUP.findVirtual(usfClass, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class));
					MethodHandle theUnsafe = TRUSTED_LOOKUP.findStaticGetter(usfClass, "theUnsafe", usfClass);
					byte[] code = new ClassWriter()
						.set(0x34, 0x21, JavaVM.CONSTANT[0], mai, null)
						.method(new MethodWriter()
							.set(AccessFlag.PUBLIC, "<init>", "()V")
							.attribute(new CodeWriter()
								.instruction(Opcodes.ALOAD_0)
								.method(Opcodes.INVOKESPECIAL, mai, "<init>", "()V", false)
								.instruction(Opcodes.RETURN)
								.max(1, 1)
							)
						)
						.toByteArray();
					usfDefineClass.invoke(theUnsafe.invoke(), null, code, 0, code.length, null, null);
				}
			}

			/*
			 * Method Handle Invoker
			 */
			{
				Class<?> handleInvoker;
				try
				{
					handleInvoker = Class.forName("org.mve.invoke.MethodHandleInvoker");
				}
				catch (Throwable t)
				{
					ClassWriter cw = new ClassWriter();
					cw.set(52, AccessFlag.SUPER | AccessFlag.PUBLIC, "org/mve/invoke/MethodHandleInvoker", "java/lang/Object", new String[]{"org/mve/invoke/ReflectionAccessor"});
					/*
					 * MethodHandleInvoker();
					 */
					{
						cw.method(new MethodWriter()
							.set(AccessFlag.PUBLIC, "<init>", "()V")
							.attribute(new CodeWriter()
								.instruction(Opcodes.ALOAD_0)
								.method(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
								.instruction(Opcodes.RETURN)
								.max(1, 1)
							)
						);
					}
					/*
					 * Object invoke(Object...);
					 */
					{
						cw.method(new MethodWriter()
							.set(AccessFlag.PUBLIC | AccessFlag.VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;")
							.attribute(new CodeWriter()
								.instruction(Opcodes.ALOAD_1)
								.instruction(Opcodes.ICONST_0)
								.instruction(Opcodes.AALOAD)
								.type(Opcodes.CHECKCAST, "java/lang/invoke/MethodHandle")
								.instruction(Opcodes.ALOAD_1)
								.instruction(Opcodes.ICONST_1)
								.instruction(Opcodes.ALOAD_1)
								.instruction(Opcodes.ARRAYLENGTH)
								.method(Opcodes.INVOKESTATIC, "java/util/Arrays", "copyOfRange", MethodType.methodType(Object[].class, Object[].class, int.class, int.class).toMethodDescriptorString(), false)
								.method(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeWithArguments", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false)
								.instruction(Opcodes.ARETURN)
								.max(4, 2)
							)
						);
					}
					byte[] code = cw.toByteArray();
					handleInvoker = (Class<?>) DEFINE.invoke(ReflectionFactory.class.getClassLoader(), null, code, 0, code.length);
				}
				METHOD_HANDLE_INVOKER = (ReflectionAccessor<Object>) handleInvoker.getDeclaredConstructor().newInstance();
			}

			/*
			 * Unsafe wrapper
			 */
			{
				String className = "org/mve/invoke/UnsafeWrapper";
				Class<?> clazz;
				try
				{
					clazz = Class.forName(className.replace('/', '.'));
				}
				catch (Throwable t)
				{
					UnsafeBuilder builder = new UnsafeBuilder(TRUSTED_LOOKUP);
					byte[] code = builder.build();
					clazz = (Class<?>) DEFINE.invoke(ReflectionFactory.class.getClassLoader(), null, code, 0, code.length);
					builder.post(clazz);
				}

				UNSAFE = (Unsafe) usf.allocateInstance(clazz);
				UNSAFE.putObject(Generator.class, UNSAFE.staticFieldOffset(Generator.class.getDeclaredField("UNSAFE")), UNSAFE);
			}

			/*
			 * accessor
			 */
			{
				Class<?> c;
				try
				{
					c = Class.forName("org.mve.invoke.ReflectionMagicAccessor");
				}
				catch (Throwable t)
				{
					byte[] code = MagicAccessorBuilder.build(JavaVM.CONSTANT, JavaVM.VERSION, openJ9VM).toByteArray();
					c = UNSAFE.defineClass(null, code, 0, code.length, ReflectionFactory.class.getClassLoader(), null);
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
