package org.mve.invoke;

import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.attribute.SourceWriter;
import org.mve.asm.file.AccessFlag;
import org.mve.invoke.common.AllocatorGenerator;
import org.mve.invoke.common.ConstructorAccessorGenerator;
import org.mve.invoke.common.FieldAccessorGenerator;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.MagicAccessorBuilder;
import org.mve.invoke.common.MagicAllocatorGenerator;
import org.mve.invoke.common.MagicConstructorAccessorGenerator;
import org.mve.invoke.common.MagicMethodAccessorGenerator;
import org.mve.invoke.common.MethodAccessorGenerator;
import org.mve.invoke.common.NativeAllocatorGenerator;
import org.mve.invoke.common.NativeConstructorAccessorGenerator;
import org.mve.invoke.common.NativeMethodAccessorGenerator;
import org.mve.invoke.common.UninitializedException;
import org.mve.invoke.common.UnsafeBuilder;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
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

	/**
	 * Some useful methods
	 */
	public static final MagicAccessor ACCESSOR;
	public static final int
		KIND_INVOKE_VIRTUAL		= 0,
		KIND_INVOKE_SPECIAL		= 1,
		KIND_INVOKE_STATIC		= 2,
		KIND_INVOKE_INTERFACE	= 3,
		KIND_GET				= 4,
		KIND_PUT				= 5;

	public static boolean save = false;

	private static final String[] CONSTANT_POOL = new String[4];
	private static final Map<Field, FieldAccessor<?>> GENERATED_FIELD_ACCESSOR = new ConcurrentHashMap<>();
	private static final Map<String, MethodAccessor<?>> GENERATED_METHOD_ACCESSOR = new ConcurrentHashMap<>();
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
			.set(0x34, 0x21, className, "java/lang/Object", new String[]{Generator.getType(ReflectionAccessor.class)})
			.addAttribute(new SourceWriter("Thrower.java"))
			.addMethod(new MethodWriter()
				.set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;")
				.addAttribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ICONST_0)
					.instruction(Opcodes.AALOAD)
					.type(Opcodes.CHECKCAST, "java/lang/Throwable")
					.instruction(Opcodes.ATHROW)
					.max(2, 2)
				)
			);
		return (ReflectionAccessor<Void>) UNSAFE.allocateInstance(defineAnonymous(ReflectionFactory.class, cw));
	}

	public static <T> ReflectionAccessor<T> constant(T value)
	{
		String className = "org/mve/invoke/ConstantValue";
		ClassWriter cw = new ClassWriter()
			.set(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL | AccessFlag.ACC_SUPER, className, "java/lang/Object", new String[]{Generator.getType(ReflectionAccessor.class)})
			.addAttribute(new SourceWriter("ConstantValue.java"))
			.addField(new FieldWriter()
				.set(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_FINAL, "0", "Ljava/lang/Object;")
			)
			.addMethod(new MethodWriter()
				.set(AccessFlag.ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V")
				.addAttribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.instruction(Opcodes.DUP)
					.method(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
					.instruction(Opcodes.ALOAD_1)
					.field(Opcodes.PUTFIELD, className, "0", "Ljava/lang/Object;")
					.instruction(Opcodes.RETURN)
					.max(2, 2)
				)
			)
			.addMethod(new MethodWriter()
				.set(AccessFlag.ACC_PUBLIC, "invoke", "()Ljava/lang/Object;")
				.addAttribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.field(Opcodes.GETFIELD, className, "0", "Ljava/lang/Object;")
					.instruction(Opcodes.ARETURN)
					.max(1, 2)
				)
			);
		return ACCESSOR.construct(defineAnonymous(ReflectionFactory.class, cw), new Class[]{Object.class}, new Object[]{value});
	}

	public static <T> EnumHelper<T> enumHelper(Class<?> target)
	{
		return (EnumHelper<T>) GENERATED_ENUM_HELPER.computeIfAbsent(target, (k) -> new PolymorphismFactory<>(EnumHelper.class).enumHelper(k).allocate());
	}

	private static Class<?> defineAnonymous(Class<?> host, ClassWriter bytecode)
	{
		byte[] code = bytecode.toByteArray();

		if (save)
		{
			try
			{
				File file = new File(bytecode.getName().concat(".class"));
				FileOutputStream out = new FileOutputStream(file);
				out.write(code);
				out.flush();
				out.close();
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
		}

		return UNSAFE.defineAnonymousClass(host, code, null);
	}

	private static <T> MethodAccessor<T> generic(Method target, int kind)
	{
		String handle = Generator.getType(target.getDeclaringClass()) +
			"." +
			target.getName() +
			":" +
			MethodType.methodType(target.getReturnType(), target.getParameterTypes()).toMethodDescriptorString() +
			"-" +
			Generator.kind(kind);

		MethodAccessor<T> generated = (MethodAccessor<T>) GENERATED_METHOD_ACCESSOR.get(handle);
		if (generated != null)
		{
			return generated;
		}

		MethodAccessorGenerator generator;
		if (Generator.isVMAnonymousClass(target.getDeclaringClass()))
		{
			generator = new NativeMethodAccessorGenerator(target, kind);
		}
		else
		{
			generator = new MagicMethodAccessorGenerator(target, kind);
		}
		generator.generate();

		ClassWriter bytecode = generator.bytecode();
		Class<?> clazz = target.getDeclaringClass();
		boolean access = Generator.checkAccessible(clazz.getClassLoader());
		Class<?> c = defineAnonymous(access ? clazz : ReflectionFactory.class, bytecode);
		generator.postgenerate(c);

		generated = (MethodAccessor<T>) UNSAFE.allocateInstance(c);
		GENERATED_METHOD_ACCESSOR.put(handle, generated);

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
		Class<?> clazz = target.getDeclaringClass();
		boolean acc = Generator.checkAccessible(clazz.getClassLoader());

		FieldAccessorGenerator generator = new FieldAccessorGenerator(target);
		generator.generate();

		ClassWriter bytecode = generator.bytecode();
		Class<?> c = defineAnonymous(acc ? clazz : ReflectionFactory.class, bytecode);
		generator.postgenerate(c);
		generated = (FieldAccessor<T>) UNSAFE.allocateInstance(c);
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
		Class<?> clazz = target.getDeclaringClass();
		boolean access = Generator.checkAccessible(clazz.getClassLoader());
		
		ConstructorAccessorGenerator generator;
		if (Generator.isVMAnonymousClass(clazz))
		{
			generator = new NativeConstructorAccessorGenerator(target);
		}
		else
		{
			generator = new MagicConstructorAccessorGenerator(target);
		}
		generator.generate();
		
		ClassWriter bytecode = generator.bytecode();
		Class<?> c = defineAnonymous(access ? clazz : ReflectionFactory.class, bytecode);
		generator.postgenerate(c);
		generated = (ConstructorAccessor<T>) UNSAFE.allocateInstance(c);
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
		if (Generator.isVMAnonymousClass(target))
		{
			generator = new NativeAllocatorGenerator(target);
		}
		else
		{
			generator = new MagicAllocatorGenerator(target);
		}
		generator.generate();
		
		ClassWriter bytecode = generator.bytecode();
		Class<?> c = defineAnonymous(access ? target : ReflectionFactory.class, bytecode);
		generator.postgenerate(c);
		generated = (ReflectionAccessor<T>) UNSAFE.allocateInstance(c);
		GENERATED_ALLOCATOR.put(target, generated);
		return generated;
	}

	static
	{
		try
		{
			RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
			String vm = bean.getVmVendor();
			boolean openJ9VM = vm.equals("Eclipse OpenJ9");
			URL url = ClassLoader.getSystemClassLoader().getResource("java/lang/Object.class");
			if (url == null) throw new NullPointerException();
			InputStream in = url.openStream();
			if (6 != in.skip(6)) throw new UnknownError();
			int majorVersion = new DataInputStream(in).readUnsignedShort();
			in.close();

			/*
			 * MagicAccessorImpl
			 */
			String mai;
			{
				if (majorVersion <= 0X34) mai = "sun/reflect/MagicAccessorImpl";
				else mai = "jdk/internal/reflect/MagicAccessorImpl";
				CONSTANT_POOL[0] = "java/lang/MagicAccessorFactory";
			}

			/*
			 * Hidden stack
			 */
			{
				CONSTANT_POOL[1] = majorVersion < 57 ? "Ljava/lang/invoke/LambdaForm$Hidden;" : "Ljdk/internal/vm/annotation/Hidden;";
				CONSTANT_POOL[2] = majorVersion == 0x34 ? "Ljava/lang/invoke/ForceInline;" : "Ljdk/internal/vm/annotation/ForceInline;";
				CONSTANT_POOL[3] = "Ljava/lang/invoke/LambdaForm$Compiled;";
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
					Class.forName(CONSTANT_POOL[0].replace('/', '.'));
				}
				catch (Throwable t)
				{
					Class<?> usfClass = Class.forName(majorVersion > 0x34 ? "jdk.internal.misc.Unsafe" : "sun.misc.Unsafe");
					MethodHandle usfDefineClass = TRUSTED_LOOKUP.findVirtual(usfClass, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class));
					MethodHandle theUnsafe = TRUSTED_LOOKUP.findStaticGetter(usfClass, "theUnsafe", usfClass);
					byte[] code = new ClassWriter()
						.set(0x34, 0x21, CONSTANT_POOL[0], mai, null)
						.addMethod(new MethodWriter()
							.set(AccessFlag.ACC_PUBLIC, "<init>", "()V")
							.addAttribute(new CodeWriter()
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
					cw.set(52, AccessFlag.ACC_SUPER | AccessFlag.ACC_PUBLIC, "org/mve/invoke/MethodHandleInvoker", "java/lang/Object", new String[]{"org/mve/invoke/ReflectionAccessor"});
					/*
					 * MethodHandleInvoker();
					 */
					{
						cw.addMethod(new MethodWriter()
							.set(AccessFlag.ACC_PUBLIC, "<init>", "()V")
							.addAttribute(new CodeWriter()
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
						cw.addMethod(new MethodWriter()
							.set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;")
							.addAttribute(new CodeWriter()
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
					byte[] code = UnsafeBuilder.build(majorVersion, CONSTANT_POOL, vm).toByteArray();
					clazz = (Class<?>) DEFINE.invoke(ReflectionFactory.class.getClassLoader(), null, code, 0, code.length);
				}

				UNSAFE = (Unsafe) usf.allocateInstance(clazz);
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
					byte[] code = MagicAccessorBuilder.build(CONSTANT_POOL, majorVersion, openJ9VM).toByteArray();
					FileOutputStream out = new FileOutputStream("MagicAccessor.class");
					out.write(code);
					out.flush();
					out.close();
					c = UNSAFE.defineClass(null, code, 0, code.length, ReflectionFactory.class.getClassLoader(), null);
				}
				ACCESSOR = (MagicAccessor) UNSAFE.allocateInstance(c);
			}
		}
		catch (Throwable t)
		{
			throw new UninitializedException(t);
		}
	}
}
