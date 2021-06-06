package org.mve.invoke;

import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.attribute.SourceWriter;
import org.mve.asm.file.AccessFlag;

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

/**
 * ReflectionFactory is the core class of ReflectionFX
 * All reflection operations must be implemented with this class
 *
 * The principle of all reflection is to bypass the bytecode
 * check by inheriting MagicAccessorImpl
 *
 * it includes:
 * @see ReflectionAccessor
 * ReflectionAccessor is a universal reflection interface
 * Realize reflection by implementing methods in interface through ReflectionFactory
 * ReflectionFactory provides some methods to return ReflectionAccessor
 * ReflectionAccessor declares a parameterless method
 * @see ReflectionAccessor#invoke()
 * Call this method when reflecting a parameterless static method
 * or reflection to obtain a static field to skip the construction
 * of variable parameters
 *
 * Passing parameters through variable parameters when reflecting with ReflectionAccessor
 * @see ReflectionAccessor#invoke(Object...)
 *
 * When reflecting a static method, you can directly pass in the method parameters
 *
 * When reflecting non-static methods, you must first pass in the object to be called,
 * and then pass in the method parameters
 * When the method name is "<init>", it means that the construction method of the object to be called,
 * of course, the object to be called must also be passed in
 *
 * Unlike the reflection library that comes with Java, it can reflect the
 * construction method of already constructed objects
 *
 *
 *
 * @see EnumHelper
 * EnumHelper is an interface designed to manipulate
 * enumeration lists in enumeration classes
 *
 * You can construct new enumeration instances,
 * add/delete/modify enumeration lists,
 * and this interface does not support modification
 * for enumeration items in the enumeration class.
 *
 * @see EnumHelper#construct(String)
 * Construct a new enumeration instance with the provided name
 *
 * @see EnumHelper#construct(String, int)
 * Construct a new enumeration instance with the provided name and ordinal
 *
 * @see EnumHelper#values()
 * Get enumeration list
 * Unlike the static values method of the enumeration class,
 * this method does not copy the list, the returned array
 * is a reference to the list in the enumeration class
 *
 * @see EnumHelper#values(Object[])
 * Replace the enumeration list in the enumeration class
 * with the enumeration list passed in
 *
 * @see EnumHelper#add(Object)
 * Add an enumeration item to the enumeration list
 *
 * @see EnumHelper#remove(int)
 * Delete the enumeration item with the specified index in the enumeration list
 *
 * ReflectionFactory provides a method to return EnumHelper
 * @see ReflectionFactory#getEnumHelper(Class)
 *
 *
 * ReflectionFX provides a new reflection implementation
 * It allows the user to customize the interface according
 * to the specifications, and implement the methods declared
 * in the interface through ReflectionFactory to achieve reflection
 *
 * I call this reflection implementation dynamic binding
 *
 * Just like java determines the method address of a virtual
 * method at runtime, it allows to determine which method to
 * call with a method in the interface at runtime
 *
 * Dynamic binding can also reflect field and construct objects
 *
 * Compared with ReflectionAccessor, when customizing
 * a dynamically bound interface, the parameter list
 * of the methods in the interface is clear, and there
 * is no need to box and unbox the primitive types, nor does
 * it need to construct variable parameters
 *
 *            *****************************************
 *
 * When using dynamic binding, an interface can only bind elements in the same class
 *
 *            *****************************************
 *
 * Binding through ReflectionFactory
 * Instantiate a ReflectionFactory object
 * @see ReflectionFactory#ReflectionFactory(Class, Class)
 *
 * Several binding methods are provided in ReflectionFactory
 *
 * @see ReflectionFactory#method(MethodKind, MethodKind, int)
 * Bind a method
 *
 * @see ReflectionFactory#field(MethodKind, String, int)
 * Bind a field
 *
 * @see ReflectionFactory#instantiation(MethodKind)
 * Only allocate an object, do not call the constructor
 *
 * @see ReflectionFactory#construct(MethodKind, MethodKind)
 * Allocate an object and call constructor
 *
 * @see ReflectionFactory#enumHelper()
 * Add EnumHelper binding
 *
 * When binding a method or field, you need to pass in a "kind" to specify the call type
 * Kinds available when binding methods:
 * @see ReflectionFactory#KIND_INVOKE_VIRTUAL
 * Standard call for non-static methods of kind
 *
 * @see ReflectionFactory#KIND_INVOKE_SPECIAL
 * Ignore method override call method
 *
 * @see ReflectionFactory#KIND_INVOKE_INTERFACE
 * Call an abstract method
 *
 * @see ReflectionFactory#KIND_INVOKE_STATIC
 * Call a static method
 *
 * Kinds available when binding fields:
 * @see ReflectionFactory#KIND_PUT
 * Modify the field value
 *
 * @see ReflectionFactory#KIND_GET
 * Get the field value
 *
 * When binding, the MethodKind of the binding site must be passed first,
 * and then the information of the bound element
 *
 * Usage:
 * Suppose there is such a class to reflect the call
 *
 * public class PrintClass {
 *     private void print(String text) {
 *         System.out.println(text);
 *     }
 *
 *     private int read(byte[] buf) {
 *         return System.in.read(buf, 0, buf.length);
 *     }
 * }
 *
 * First declare an interface, declare the binding site in the interface
 *
 * public interface CallInterface {
 *     // This method will be bound to the print method
 *     // Because the target method is a non-static method, the first parameter needs to be passed in a target object
 *     void callPrint(PrintClass obj, String text);
 *
 *     // This method will be bound to the read method
 *     int callRead(PrintClass obj, byte[] buf);
 * }
 *
 * Then use the ReflectionFactory binding method
 *
 * ReflectionFactory factory = new ReflectionFactory(CallInterface.class, PrintClass.class);
 * factory.method(new MethodKind("callPrint", void.class, PrintClass.class, String.class), new MethodKind("print", void.class, String.class), ReflectionFactory.KIND_INVOKE_VIRTUAL);
 * factory.method(new MethodKind("callRead", int.class, PrintClass.class, byte[].class), new MethodKind("read", int.class, byte[].class), ReflectionFactory.KIND_INVOKE_VIRTUAL);
 * // Finally call the {@link ReflectionFactory#allocate()} method to complete the binding and return the instance
 * CallInterface callsite = factory.allocate();
 *
 *
 *        **************************************
 *
 * Dynamic binding interface method declaration specification:
 * When binding a static method:
 *
 * public class Something {
 *     private static int cal(int a, int b) {
 *         return a + b;
 *     }
 * }
 *
 * The return value and parameter list of the method declaration
 * in the interface should be the same as the return value and
 * parameter list of the bound method
 *
 * public interface BindSite {
 *     int callCal(int a, int b);
 * }
 *
 *
 * When binding a non-static method:
 *
 * public class Something {
 *     public int cal(int a, int b) {
 *         return a + b;
 *     }
 * }
 *
 * The return value of the method declaration in the interface
 * must be the same as the return value type of the bound method
 * The first parameter in the parameter list of the binding site
 * must specify the object passed into the class of the bound method,
 * and then declare the same parameter list as the bound method
 *
 * public interface BindSite {
 *     int callCal(Something obj, int a, int b);
 * }
 *
 *
 * When binding a static field:
 *
 * public class Something {
 *     private static String some;
 * }
 *
 * When using the PUT kind, the return value needs to be defined
 * as void, and the parameter list is declared to pass in a value
 * of the same type as the bound field
 *
 * When using the SET kind, the return value is declared as the
 * type of the bound field, and the parameter list is empty
 *
 * public interface BindSite {
 *     void put(String str);
 *
 *     String get();
 * }
 *
 *
 * When binding a non-static field:
 *
 * public class Something {
 *     private String some;
 * }
 *
 * When using the PUT type, the return value is declared as void,
 * the first parameter of the parameter list is declared as the
 * changed object, and the second parameter is declared as the
 * type of the bound field
 *
 * When using the SET type, the return value is declared as the
 * type of the bound field, the parameter list has only one parameter,
 * and the type is the object to be obtained
 *
 * public interface BindSite {
 *     void put(Something obj, String str);
 *
 *     String get(Something obj);
 * }
 *
 *
 * When binding to instantiation:
 *
 * public class Something {}
 *
 * The return value type is the type of the target class, the parameter list is empty
 * Any constructor of the target class will not be called
 *
 * public interface BindSite {
 *     Something allocateSomething();
 * }
 *
 *
 * When binding a constructor:
 *
 * public class Something {
 *     private Something(int a, double b) {
 *     }
 * }
 *
 * The return value is the type of the target class,
 * the parameter list is the same as the parameter
 * list of the bound constructor
 *
 * public interface BindSite {
 *     Something construct(int a, double b);
 * }
 *
 *
 * When binding an enum helper:
 *
 * public enum Something {
 * }
 *
 * When binding enum helper, you can let the binding
 * interface inherit {@link EnumHelper}, or you can define your own method
 * When you define a method yourself, the method declaration needs to be the
 * same as the method declaration in {@link EnumHelper}.
 * The generic T can be replaced with the type of the target enumeration,
 * and you can optionally define one or more methods such as adding only the
 * {@link  EnumHelper#construct(String)} method
 *
 * public interface BindSite extends EnumHelper<Something> {
 * }
 *
 * or
 *
 * public interface BindSite {
 *     Something construct(String name);
 *     Something construct(String name, int ordinal);
 * }
 *
 *        **************************************
 *
 *
 *
 *
 *
 * @see ReflectionFactory#constant(Object)
 * @see ReflectionFactory#throwException()
 *
 *
 *
 *
 * This class provides some practical tools
 * @see ReflectionFactory#UNSAFE
 * @see ReflectionFactory#TRUSTED_LOOKUP
 * @see ReflectionFactory#METHOD_HANDLE_INVOKER
 * @see ReflectionFactory#ACCESSOR
 *
 * @author MeiVinEight QQ 3390038158
 */
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
	 * It can find any element regardless of access rights
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
	private static final Map<Method, MethodAccessor<?>> GENERATED_METHOD_ACCESSOR = new ConcurrentHashMap<>();
	private static final Map<Constructor<?>, ConstructorAccessor<?>> GENERATED_CONSTRUCTOR_ACCESSOR = new ConcurrentHashMap<>();
	private static final Map<Class<?>, ReflectionAccessor<?>> GENERATED_ALLOCATOR = new ConcurrentHashMap<>();
	private static final Map<Class<?>, EnumHelper<?>> GENERATED_ENUM_HELPER = new ConcurrentHashMap<>();

	private final DynamicBind bind;

	/**
	 * Construct a ReflectionFactory instance
	 * It can be used to complete dynamic binding
	 * @param handle Dynamically bound interface and must be an interface, generally a custom interface
	 * @param target Dynamically bound target class
	 */
	public ReflectionFactory(Class<?> handle, Class<?> target)
	{
		UNSAFE.ensureClassInitialized(target);
		if (Generator.isVMAnonymousClass(target))
		{
			this.bind = new NativeDynamicBind(handle, target);
		}
		else
		{
			this.bind = new MagicDynamicBind(handle, target);
		}
	}

	/**
	 * Bind a method to the interface
	 * @param implementation Method declared in the interface
	 * @param invocation Bound method
	 * @param kind Method call kind, it can be
	 *             {@link ReflectionFactory#KIND_INVOKE_VIRTUAL}
	 *             {@link ReflectionFactory#KIND_INVOKE_SPECIAL}
	 *             {@link ReflectionFactory#KIND_INVOKE_STATIC}
	 *             {@link ReflectionFactory#KIND_INVOKE_INTERFACE}
	 * @return Chained call return
	 */
	public ReflectionFactory method(MethodKind implementation, MethodKind invocation, int kind)
	{
		this.bind.method(implementation, invocation, kind);
		return this;
	}

	/**
	 * Bind a field to the interface method
	 * @param implementation Dynamically bound interface and must be an interface, generally a custom interface
	 * @param operation The name of the bound field
	 * @param kind Field operation type, it can be
	 *             {@link ReflectionFactory#KIND_GET}
	 *             {@link ReflectionFactory#KIND_PUT}
	 * @return Chained call return
	 */
	public ReflectionFactory field(MethodKind implementation, String operation, int kind)
	{
		this.bind.field(implementation, operation, kind);
		return this;
	}

	/**
	 * Only allocate an object, do not call the constructor
	 * @param implementation Dynamically bound interface and must be an interface, generally a custom interface
	 * @return Chained call return
	 */
	public ReflectionFactory instantiation(MethodKind implementation)
	{
		this.bind.instantiation(implementation);
		return this;
	}

	/**
	 * Allocate an object and call constructor
	 * @param implementation Dynamically bound interface and must be an interface, generally a custom interface
	 * @param invocation The bound constructor
	 * @return Chained call return
	 */
	public ReflectionFactory construct(MethodKind implementation, MethodKind invocation)
	{
		this.bind.construct(implementation, invocation);
		return this;
	}

	/**
	 * Add EnumHelper binding
	 * @return Chained call return
	 */
	public ReflectionFactory enumHelper()
	{
		this.bind.enumHelper();
		return this;
	}

	/**
	 * Complete the binding and return an instance of the interface implementation class
	 * @param <T> Same type as "handle" when the constructor {@link ReflectionFactory#ReflectionFactory(Class, Class)} is called
	 * @return The instance of "handle"
	 */
	public <T> T allocate()
	{
		ClassWriter bytecode = this.bind.bytecode();
		Class<?> c = defineAnonymous(this.bind.define(), bytecode);
		this.bind.postgenerate(c);
		T value = (T) UNSAFE.allocateInstance(c);
		ACCESSOR.initialize(value);
		return value;
	}

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
					.addInstruction(Opcodes.ALOAD_1)
					.addInstruction(Opcodes.ICONST_0)
					.addInstruction(Opcodes.AALOAD)
					.addTypeInstruction(Opcodes.CHECKCAST, "java/lang/Throwable")
					.addInstruction(Opcodes.ATHROW)
					.setMaxs(2, 2)
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
					.addInstruction(Opcodes.ALOAD_0)
					.addInstruction(Opcodes.DUP)
					.addMethodInstruction(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
					.addInstruction(Opcodes.ALOAD_1)
					.addFieldInstruction(Opcodes.PUTFIELD, className, "0", "Ljava/lang/Object;")
					.addInstruction(Opcodes.RETURN)
					.setMaxs(2, 2)
				)
			)
			.addMethod(new MethodWriter()
				.set(AccessFlag.ACC_PUBLIC, "invoke", "()Ljava/lang/Object;")
				.addAttribute(new CodeWriter()
					.addInstruction(Opcodes.ALOAD_0)
					.addFieldInstruction(Opcodes.GETFIELD, className, "0", "Ljava/lang/Object;")
					.addInstruction(Opcodes.ARETURN)
					.setMaxs(1, 2)
				)
			);
		return ACCESSOR.construct(defineAnonymous(ReflectionFactory.class, cw), new Class[]{Object.class}, new Object[]{value});
	}

	public static <T> EnumHelper<T> getEnumHelper(Class<?> target)
	{
		return (EnumHelper<T>) GENERATED_ENUM_HELPER.computeIfAbsent(target, (k) -> new ReflectionFactory(EnumHelper.class, k).enumHelper().allocate());
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
		MethodAccessor<T> generated = (MethodAccessor<T>) GENERATED_METHOD_ACCESSOR.get(target);
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
								.addInstruction(Opcodes.ALOAD_0)
								.addMethodInstruction(Opcodes.INVOKESPECIAL, mai, "<init>", "()V", false)
								.addInstruction(Opcodes.RETURN)
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
								.addInstruction(Opcodes.ALOAD_0)
								.addMethodInstruction(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
								.addInstruction(Opcodes.RETURN)
								.setMaxs(1, 1)
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
								.addInstruction(Opcodes.ALOAD_1)
								.addInstruction(Opcodes.ICONST_0)
								.addInstruction(Opcodes.AALOAD)
								.addTypeInstruction(Opcodes.CHECKCAST, "java/lang/invoke/MethodHandle")
								.addInstruction(Opcodes.ALOAD_1)
								.addInstruction(Opcodes.ICONST_1)
								.addInstruction(Opcodes.ALOAD_1)
								.addInstruction(Opcodes.ARRAYLENGTH)
								.addMethodInstruction(Opcodes.INVOKESTATIC, "java/util/Arrays", "copyOfRange", MethodType.methodType(Object[].class, Object[].class, int.class, int.class).toMethodDescriptorString(), false)
								.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeWithArguments", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false)
								.addInstruction(Opcodes.ARETURN)
								.setMaxs(4, 2)
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
					byte[] code = MagicAccessorBuilder.build(CONSTANT_POOL, openJ9VM).toByteArray();
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
