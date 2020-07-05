package org.mve.invoke;

import org.mve.util.asm.AnnotationWriter;
import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.Marker;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.OperandStack;
import org.mve.util.asm.Type;
import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.attribute.RuntimeVisibleAnnotationsWriter;
import org.mve.util.asm.attribute.SourceWriter;
import org.mve.util.asm.file.AccessFlag;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
@SuppressWarnings({"unchecked"})
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

	private static final String[] CONSTANT_POOL = new String[6];

	private final ClassWriter generator = new ClassWriter();
	private final Class<?> target;
	private final Class<?> define;

	/**
	 * Construct a ReflectionFactory instance
	 * It can be used to complete dynamic binding
	 * @param handle Dynamically bound interface and must be an interface, generally a custom interface
	 * @param target Dynamically bound target class
	 */
	public ReflectionFactory(Class<?> handle, Class<?> target)
	{
		Class<?> c = checkAccessible(handle.getClassLoader(), target.getClassLoader()) ? handle : target;
		this.define = checkAccessible(c.getClassLoader()) ? c : ReflectionFactory.class;
		this.target = target;
		this.generator.set(0x34, 0x21, this.define.getPackage().getName().concat("/").concat(handle.getSimpleName()).replace('.', '/'), CONSTANT_POOL[0], new String[]{getType(handle)});
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
		CodeWriter code = this.generator.addMethod(AccessFlag.ACC_PUBLIC, implementation.name(), implementation.type().toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
				.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
				.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
				.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode();
		int local = 1;
		Class<?>[] params = implementation.type().parameterArray();
		for (Class<?> c : params)
		{
			if (integer(c)) code.addLocalVariableInstruction(Opcodes.ILOAD, local);
			else if (c == long.class) code.addLocalVariableInstruction(Opcodes.LLOAD, local);
			else if (c == float.class) code.addLocalVariableInstruction(Opcodes.FLOAD, local);
			else if (c == double.class) code.addLocalVariableInstruction(Opcodes.DLOAD, local);
			else code.addLocalVariableInstruction(Opcodes.ALOAD, local);
			local += typeSize(c);
		}
		code.addMethodInstruction(0xB6 + kind, getType(target), invocation.name(), invocation.type().toMethodDescriptorString(), kind == KIND_INVOKE_INTERFACE);
		Class<?> c = implementation.type().returnType();
		if (c == void.class)
		{
			if (invocation.type().returnType() != void.class)
			{
				if (typeSize(invocation.type().returnType()) == 2) code.addInstruction(Opcodes.POP2);
				else code.addInstruction(Opcodes.POP);
			}
			code.addInstruction(Opcodes.RETURN);
		}
		else if (integer(c)) code.addInstruction(Opcodes.IRETURN);
		else if (c == long.class) code.addInstruction(Opcodes.LRETURN);
		else if (c == float.class) code.addInstruction(Opcodes.FRETURN);
		else if (c == double.class) code.addInstruction(Opcodes.DRETURN);
		else code.addInstruction(Opcodes.ARETURN);
		code.setMaxs(Math.max(typeSize(invocation.type().returnType()), local-1), local);
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
		Field f = ACCESSOR.getField(target, operation);
		Class<?> type = f.getType();
		boolean isStatic = Modifier.isStatic(f.getModifiers());
		CodeWriter code = this.generator.addMethod(AccessFlag.ACC_PUBLIC, implementation.name(), implementation.type().toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode();
		code.setMaxLocals(1 + (isStatic ? 0 : 1) + typeSize(type));
		boolean isFinal = Modifier.isFinal(f.getModifiers());
		int opcode = kind + 0xAE;
		opcode += isStatic ? 0 : 2;
		int load;
		if (integer(type)) load = Opcodes.ILOAD_0;
		else if (type == long.class) load = Opcodes.LLOAD_0;
		else if (type == float.class) load = Opcodes.FLOAD_0;
		else if (type == double.class) load = Opcodes.DLOAD_0;
		else load = Opcodes.ALOAD_0;
		load += isStatic ? 1 : 2;
		if (kind == KIND_PUT)
		{
			if (isFinal)
			{
				long offset = isStatic ? UNSAFE.staticFieldOffset(f) : UNSAFE.objectFieldOffset(f);
				code.addFieldInstruction(Opcodes.GETSTATIC, getType(ReflectionFactory.class), "UNSAFE", getDescriptor(Unsafe.class));
				if (isStatic) code.addConstantInstruction(Opcodes.LDC_W, new Type(target));
				else code.addInstruction(Opcodes.ALOAD_1);
				code.addConstantInstruction(Opcodes.LDC2_W, offset);
				code.addInstruction(load);
				if (type == byte.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putByteVolatile", "(Ljava/lang/Object;JB)V", true);
				else if (type == short.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putShortVolatile", "(Ljava/lang/Object;JS)V", true);
				else if (type == int.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putIntVolatile", "(Ljava/lang/Object;JI)V", true);
				else if (type == long.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putLongVolatile", "(Ljava/lang/Object;JJ)V", true);
				else if (type == float.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putFloatVolatile", "(Ljava/lang/Object;JF)V", true);
				else if (type == double.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putDoubleVolatile", "(Ljava/lang/Object;JD)V", true);
				else if (type == boolean.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putBooleanVolatile", "(Ljava/lang/Object;JZ)V", true);
				else if (type == char.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putCharVolatile", "(Ljava/lang/Object;JC)V", true);
				else code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true);
				code.addInstruction(Opcodes.RETURN).setMaxStack(4 + typeSize(type));
			}
			else
			{
				if (!isStatic) code.addInstruction(Opcodes.ALOAD_1);
				code
					.addInstruction(load)
					.addFieldInstruction(opcode, getType(target), f.getName(), getDescriptor(type))
					.addInstruction(Opcodes.RETURN)
					.setMaxStack(typeSize(type) + (isStatic ? 0 : 1));
			}
		}
		else if (kind == KIND_GET)
		{
			if (!isStatic) code.addInstruction(Opcodes.ALOAD_1);
			code.addFieldInstruction(opcode, getType(target), f.getName(), getDescriptor(type));
			if (integer(type)) code.addInstruction(Opcodes.IRETURN);
			else if (type == long.class) code.addInstruction(Opcodes.LRETURN);
			else if (type == float.class) code.addInstruction(Opcodes.FRETURN);
			else if (type == double.class) code.addInstruction(Opcodes.DRETURN);
			else code.addInstruction(Opcodes.ARETURN);
			code.setMaxStack(typeSize(type));
		}
		return this;
	}

	/**
	 * Only allocate an object, do not call the constructor
	 * @param implementation Dynamically bound interface and must be an interface, generally a custom interface
	 * @return Chained call return
	 */
	public ReflectionFactory instantiation(MethodKind implementation)
	{
		this.generator
			.addMethod(AccessFlag.ACC_PUBLIC, implementation.name(), implementation.type().toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addTypeInstruction(Opcodes.NEW, getType(target))
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 1 + parameterSize(implementation.type().parameterArray()));
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
		CodeWriter code = this.generator
			.addMethod(AccessFlag.ACC_PUBLIC, implementation.name(), implementation.type().toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addTypeInstruction(Opcodes.NEW, getType(target))
			.addInstruction(Opcodes.DUP)
			.setMaxs(2 + parameterSize(implementation.type().parameterArray()), 1 + parameterSize(implementation.type().parameterArray()));
		int local = 1;
		Class<?>[] params = implementation.type().parameterArray();
		for (Class<?> c : params)
		{
			if (integer(c)) code.addLocalVariableInstruction(Opcodes.ILOAD, local);
			else if (c == long.class) code.addLocalVariableInstruction(Opcodes.LLOAD, local);
			else if (c == float.class) code.addLocalVariableInstruction(Opcodes.FLOAD, local);
			else if (c == double.class) code.addLocalVariableInstruction(Opcodes.DLOAD, local);
			else code.addLocalVariableInstruction(Opcodes.ALOAD, local);
			local += typeSize(c);
		}
		code
			.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(target), "<init>", invocation.type().toMethodDescriptorString(), false)
			.addInstruction(Opcodes.ARETURN);
		return this;
	}

	/**
	 * Add EnumHelper binding
	 * @return Chained call return
	 */
	public ReflectionFactory enumHelper()
	{
		String values;
		FIND:
		{
			Field[] fields = ACCESSOR.getFields(this.target);
			for (Field field : fields)
			{
				int modifier = field.getModifiers();
				if (Modifier.isPrivate(modifier) && Modifier.isStatic(modifier) && Modifier.isFinal(modifier) && field.getType().isArray() && field.getType().getComponentType() == this.target)
				{
					values = field.getName();
					break FIND;
				}
			}
			ACCESSOR.throwException(new NoSuchFieldException("private static final ".concat(target.getName()).concat("[]")));
			return null;
		}
		long offset = UNSAFE.staticFieldOffset(ACCESSOR.getField(this.target, values));
		Marker m1 = new Marker();
		this.generator
			.addMethod(AccessFlag.ACC_PUBLIC, "construct", MethodType.methodType(Object.class, String.class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addTypeInstruction(Opcodes.NEW, getType(target))
			.addInstruction(Opcodes.DUP)
			.addInstruction(Opcodes.ALOAD_1)
			.addFieldInstruction(Opcodes.GETSTATIC, getType(target), values, "[".concat(getDescriptor(target)))
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Enum.class), "<init>", MethodType.methodType(void.class, String.class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(4, 2)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "construct", MethodType.methodType(this.target, String.class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addTypeInstruction(Opcodes.NEW, getType(target))
			.addInstruction(Opcodes.DUP)
			.addInstruction(Opcodes.ALOAD_1)
			.addFieldInstruction(Opcodes.GETSTATIC, getType(target), values, "[".concat(getDescriptor(target)))
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Enum.class), "<init>", MethodType.methodType(void.class, String.class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(4, 2)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "construct", MethodType.methodType(Object.class, String.class, int.class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addTypeInstruction(Opcodes.NEW, getType(target))
			.addInstruction(Opcodes.DUP)
			.addInstruction(Opcodes.ALOAD_1)
			.addInstruction(Opcodes.ILOAD_2)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Enum.class), "<init>", MethodType.methodType(void.class, String.class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(4, 3)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "construct", MethodType.methodType(this.target, String.class, int.class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addTypeInstruction(Opcodes.NEW, getType(target))
			.addInstruction(Opcodes.DUP)
			.addInstruction(Opcodes.ALOAD_1)
			.addInstruction(Opcodes.ILOAD_2)
			.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Enum.class), "<init>", MethodType.methodType(void.class, String.class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(4, 3)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "values", MethodType.methodType(Object[].class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addFieldInstruction(Opcodes.GETSTATIC, getType(target), values, "[".concat(getDescriptor(target)))
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 1)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "values", "()[".concat(getDescriptor(target)))
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addFieldInstruction(Opcodes.GETSTATIC, getType(target), values, "[".concat(getDescriptor(target)))
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 1)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "values", MethodType.methodType(void.class, Object[].class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addConstantInstruction(Opcodes.LDC_W, new Type(this.target))
			.addInstruction(Opcodes.ACONST_NULL)
			.addFieldInstruction(Opcodes.PUTFIELD, getType(Class.class), CONSTANT_POOL[1], getDescriptor(Object[].class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(this.target))
			.addInstruction(Opcodes.ACONST_NULL)
			.addFieldInstruction(Opcodes.PUTFIELD, getType(Class.class), CONSTANT_POOL[2], getDescriptor(Map.class))
			.addFieldInstruction(Opcodes.GETSTATIC, getType(ReflectionFactory.class), "UNSAFE", getDescriptor(Unsafe.class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(target))
			.addConstantInstruction(Opcodes.LDC2_W, offset)
			.addInstruction(Opcodes.ALOAD_1)
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
			.addInstruction(Opcodes.RETURN)
			.setMaxs(5, 2)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "values", "([".concat(getDescriptor(this.target)).concat(")V"))
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addConstantInstruction(Opcodes.LDC_W, new Type(this.target))
			.addInstruction(Opcodes.ACONST_NULL)
			.addFieldInstruction(Opcodes.PUTFIELD, getType(Class.class), CONSTANT_POOL[1], getDescriptor(Object[].class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(this.target))
			.addInstruction(Opcodes.ACONST_NULL)
			.addFieldInstruction(Opcodes.PUTFIELD, getType(Class.class), CONSTANT_POOL[2], getDescriptor(Map.class))
			.addFieldInstruction(Opcodes.GETSTATIC, getType(ReflectionFactory.class), "UNSAFE", getDescriptor(Unsafe.class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(target))
			.addConstantInstruction(Opcodes.LDC2_W, offset)
			.addInstruction(Opcodes.ALOAD_1)
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
			.addInstruction(Opcodes.RETURN)
			.setMaxs(5, 2)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "add", MethodType.methodType(void.class, Object.class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addConstantInstruction(Opcodes.LDC_W, new Type(this.target))
			.addInstruction(Opcodes.ACONST_NULL)
			.addFieldInstruction(Opcodes.PUTFIELD, getType(Class.class), CONSTANT_POOL[1], getDescriptor(Object[].class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(this.target))
			.addInstruction(Opcodes.ACONST_NULL)
			.addFieldInstruction(Opcodes.PUTFIELD, getType(Class.class), CONSTANT_POOL[2], getDescriptor(Map.class))
			.addFieldInstruction(Opcodes.GETSTATIC, getType(ReflectionFactory.class), "UNSAFE", getDescriptor(Unsafe.class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(target))
			.addConstantInstruction(Opcodes.LDC2_W, offset)
			.addFieldInstruction(Opcodes.GETSTATIC, getType(target), values, "[".concat(getDescriptor(target)))
			.addInstruction(Opcodes.DUP)
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(Opcodes.DUP_X1)
			.addInstruction(Opcodes.ICONST_1)
			.addInstruction(Opcodes.IADD)
			.addMethodInstruction(Opcodes.INVOKESTATIC, getType(Arrays.class), "copyOf", MethodType.methodType(Object[].class, Object[].class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.DUP_X1)
			.addInstruction(Opcodes.SWAP)
			.addInstruction(Opcodes.ALOAD_1)
			.addInstruction(Opcodes.AASTORE)
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
			.addInstruction(Opcodes.RETURN)
			.setMaxs(8, 2)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "add", MethodType.methodType(void.class, target).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addConstantInstruction(Opcodes.LDC_W, new Type(this.target))
			.addInstruction(Opcodes.ACONST_NULL)
			.addFieldInstruction(Opcodes.PUTFIELD, getType(Class.class), CONSTANT_POOL[1], getDescriptor(Object[].class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(this.target))
			.addInstruction(Opcodes.ACONST_NULL)
			.addFieldInstruction(Opcodes.PUTFIELD, getType(Class.class), CONSTANT_POOL[2], getDescriptor(Map.class))
			.addFieldInstruction(Opcodes.GETSTATIC, getType(ReflectionFactory.class), "UNSAFE", getDescriptor(Unsafe.class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(target))
			.addConstantInstruction(Opcodes.LDC2_W, offset)
			.addFieldInstruction(Opcodes.GETSTATIC, getType(target), values, "[".concat(getDescriptor(target)))
			.addInstruction(Opcodes.DUP)
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(Opcodes.DUP_X1)
			.addInstruction(Opcodes.ICONST_1)
			.addInstruction(Opcodes.IADD)
			.addMethodInstruction(Opcodes.INVOKESTATIC, getType(Arrays.class), "copyOf", MethodType.methodType(Object[].class, Object[].class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.DUP_X1)
			.addInstruction(Opcodes.SWAP)
			.addInstruction(Opcodes.ALOAD_1)
			.addInstruction(Opcodes.AASTORE)
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
			.addInstruction(Opcodes.RETURN)
			.setMaxs(8, 2)
			.getClassWriter()
			.addMethod(AccessFlag.ACC_PUBLIC, "remove", MethodType.methodType(void.class, int.class).toMethodDescriptorString())
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addConstantInstruction(Opcodes.LDC_W, new Type(this.target))
			.addInstruction(Opcodes.ACONST_NULL)
			.addFieldInstruction(Opcodes.PUTFIELD, getType(Class.class), CONSTANT_POOL[1], getDescriptor(Object[].class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(this.target))
			.addInstruction(Opcodes.ACONST_NULL)
			.addFieldInstruction(Opcodes.PUTFIELD, getType(Class.class), CONSTANT_POOL[2], getDescriptor(Map.class))
			.addFieldInstruction(Opcodes.GETSTATIC, getType(ReflectionFactory.class), "UNSAFE", getDescriptor(Unsafe.class))
			.addConstantInstruction(Opcodes.LDC_W, new Type(target))
			.addConstantInstruction(Opcodes.LDC2_W, offset)
			.addFieldInstruction(Opcodes.GETSTATIC, getType(target), values, "[".concat(getDescriptor(target)))
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(Opcodes.ICONST_1)
			.addInstruction(Opcodes.ISUB)
			.addTypeInstruction(Opcodes.ANEWARRAY, getType(target))
			.addInstruction(Opcodes.DUP)
			.addFieldInstruction(Opcodes.GETSTATIC, getType(target), values, "[".concat(getDescriptor(target)))
			.addInstruction(Opcodes.SWAP)
			.addInstruction(Opcodes.ICONST_0)
			.addInstruction(Opcodes.SWAP)
			.addInstruction(Opcodes.ICONST_0)
			.addInstruction(Opcodes.ILOAD_1)
			.addMethodInstruction(Opcodes.INVOKESTATIC, getType(System.class), "arraycopy", MethodType.methodType(void.class, Object.class, int.class, Object.class, int.class, int.class).toMethodDescriptorString(), false)
			.addInstruction(Opcodes.DUP)
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(Opcodes.ILOAD_1)
			.addJumpInstruction(Opcodes.IF_ICMPEQ, m1)
			.addInstruction(Opcodes.DUP)
			.addFieldInstruction(Opcodes.GETSTATIC, getType(target), values, "[".concat(getDescriptor(target)))
			.addInstruction(Opcodes.SWAP)
			.addInstruction(Opcodes.ILOAD_1)
			.addInstruction(Opcodes.ICONST_1)
			.addInstruction(Opcodes.IADD)
			.addInstruction(Opcodes.SWAP)
			.addInstruction(Opcodes.ILOAD_1)
			.addFieldInstruction(Opcodes.GETSTATIC, getType(target), values, "[".concat(getDescriptor(target)))
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addInstruction(Opcodes.ILOAD_1)
			.addInstruction(Opcodes.ICONST_1)
			.addInstruction(Opcodes.IADD)
			.addInstruction(Opcodes.ISUB)
			.addMethodInstruction(Opcodes.INVOKESTATIC, getType(System.class), "arraycopy", MethodType.methodType(void.class, Object.class, int.class, Object.class, int.class, int.class).toMethodDescriptorString(), false)
			.mark(m1)
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true)
			.addInstruction(Opcodes.RETURN)
			.setMaxs(12, 2);
		return this;
	}

	/**
	 * Complete the binding and return an instance of the interface implementation class
	 * @param <T> Same type as "handle" when the constructor {@link ReflectionFactory#ReflectionFactory(Class, Class)} is called
	 * @return The instance of "handle"
	 */
	public <T> T allocate()
	{
		byte[] classcode = this.generator.toByteArray();
		T value = (T) UNSAFE.allocateInstance(UNSAFE.defineAnonymousClass(this.define, classcode, null));
		ACCESSOR.initialize(value);
		return value;
	}

	public static <T> MethodAccessor<T> access(Class<?> target, String name, MethodType type, int kind)
	{
		return generic(ACCESSOR.getMethod(target, name, type.parameterArray()), kind);
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
		ClassWriter cw = new ClassWriter().addAttribute(new SourceWriter("Thrower.java"));
		cw.set(0x34, 0x21, className, "java/lang/Object", new String[]{getType(ReflectionAccessor.class)});
		cw.addSignature("Ljava/lang/Object;L"+getType(ReflectionAccessor.class)+"<Ljava/lang/Void;>;");
		CodeWriter code = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;").addCode();
		code.addInstruction(Opcodes.ALOAD_1);
		code.addInstruction(Opcodes.ICONST_0);
		code.addInstruction(Opcodes.AALOAD);
		code.addTypeInstruction(Opcodes.CHECKCAST, "java/lang/Throwable");
		code.addInstruction(Opcodes.ATHROW);
		code.setMaxs(2, 2);
		byte[] classcode = cw.toByteArray();
		return (ReflectionAccessor<Void>) UNSAFE.allocateInstance(UNSAFE.defineAnonymousClass(ReflectionFactory.class, classcode, null));
	}

	public static <T> ReflectionAccessor<T> constant(T value)
	{
		String className = "org/mve/invoke/ConstantValue";
		ClassWriter cw = new ClassWriter().addAttribute(new SourceWriter("ConstantValue.java"));
		cw.set(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL | AccessFlag.ACC_SUPER, className, "java/lang/Object", new String[]{getType(ReflectionAccessor.class)});
		cw.addSignature("Ljava/lang/Object;L"+getType(ReflectionAccessor.class)+"<"+getDescriptor(value.getClass())+">;");
		cw.addField(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_FINAL, "0", "Ljava/lang/Object;");
		CodeWriter code = cw.addMethod(AccessFlag.ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V").addCode();
		code.addInstruction(Opcodes.ALOAD_0);
		code.addInstruction(Opcodes.DUP);
		code.addMethodInstruction(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		code.addInstruction(Opcodes.ALOAD_1);
		code.addFieldInstruction(Opcodes.PUTFIELD, className, "0", "Ljava/lang/Object;");
		code.addInstruction(Opcodes.RETURN);
		code.setMaxs(2, 2);
		code = cw.addMethod(AccessFlag.ACC_PUBLIC, "invoke", "()Ljava/lang/Object;").addCode();
		code.addInstruction(Opcodes.ALOAD_0);
		code.addFieldInstruction(Opcodes.GETFIELD, className, "0", "Ljava/lang/Object;");
		code.addInstruction(Opcodes.ARETURN);
		code.setMaxs(1, 2);
		byte[] classcode = cw.toByteArray();
		return ACCESSOR.construct(UNSAFE.defineAnonymousClass(ReflectionFactory.class, classcode, null), new Class[]{Object.class}, new Object[]{value});
	}

	public static <T> EnumHelper<T> getEnumHelper(Class<?> target)
	{
		return new ReflectionFactory(EnumHelper.class, target).enumHelper().allocate();
	}

	private static void pregeneric(ClassWriter cw, AccessibleObject acc)
	{
		CodeWriter code;
		if (acc instanceof Method)
		{
			Method method = (Method) acc;
			Class<?>[] parameters = method.getParameterTypes();
			code = cw.addMethod(AccessFlag.ACC_PUBLIC, "getMethod", MethodType.methodType(Method.class).toMethodDescriptorString())
				.addCode()
				.addFieldInstruction(Opcodes.GETSTATIC, getType(ReflectionFactory.class), "ACCESSOR", getDescriptor(MagicAccessor.class))
				.addConstantInstruction(Opcodes.LDC_W, new Type(method.getDeclaringClass()))
				.addConstantInstruction(Opcodes.LDC_W, method.getName())
				.addNumberInstruction(Opcodes.BIPUSH, parameters.length)
				.addTypeInstruction(Opcodes.ANEWARRAY, getType(Class.class));
			int i = 0;
			for (Class<?> c : parameters)
			{
				code.addInstruction(Opcodes.DUP)
					.addNumberInstruction(Opcodes.BIPUSH, i++)
					.addConstantInstruction(Opcodes.LDC_W, new Type(c))
					.addInstruction(Opcodes.AASTORE);
			}
			code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(MagicAccessor.class), "getMethod", MethodType.methodType(Method.class, Class.class, String.class, Class[].class).toMethodDescriptorString(), true)
				.addInstruction(Opcodes.ARETURN)
				.setMaxs(7, 1);
		}
		else if (acc instanceof Constructor)
		{
			Constructor<?> ctr  = (Constructor<?>) acc;
			Class<?>[] parameters = ctr.getParameterTypes();
			code = cw.addMethod(AccessFlag.ACC_PUBLIC, "getConstructor", MethodType.methodType(Constructor.class).toMethodDescriptorString())
				.addCode()
				.addFieldInstruction(Opcodes.GETSTATIC, getType(ReflectionFactory.class), "ACCESSOR", getDescriptor(MagicAccessor.class))
				.addConstantInstruction(Opcodes.LDC_W, new Type(ctr.getDeclaringClass()))
				.addNumberInstruction(Opcodes.BIPUSH, parameters.length)
				.addTypeInstruction(Opcodes.ANEWARRAY, getType(Class.class));
			int i = 0;
			for (Class<?> c : parameters)
			{
				code.addInstruction(Opcodes.DUP)
					.addNumberInstruction(Opcodes.BIPUSH, i++)
					.addConstantInstruction(Opcodes.LDC_W, new Type(c))
					.addInstruction(Opcodes.AASTORE);
			}
			code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(MagicAccessor.class), "getConstructor", MethodType.methodType(Constructor.class, Class.class, Class[].class).toMethodDescriptorString(), true)
				.addInstruction(Opcodes.ARETURN)
				.setMaxs(6, 1);
		}
		else if (acc instanceof Field)
		{
			Field field = (Field) acc;
			cw.addMethod(AccessFlag.ACC_PUBLIC, "getField", MethodType.methodType(Field.class).toMethodDescriptorString())
				.addCode()
				.addFieldInstruction(Opcodes.GETSTATIC, getType(ReflectionFactory.class), "ACCESSOR", getDescriptor(MagicAccessor.class))
				.addConstantInstruction(Opcodes.LDC_W, new Type(field.getDeclaringClass()))
				.addConstantInstruction(Opcodes.LDC_W, field.getName())
				.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(MagicAccessor.class), "getField", MethodType.methodType(Field.class, Class.class, String.class).toMethodDescriptorString(), true)
				.addFieldInstruction(Opcodes.ARETURN, cw.getName(), "0", getDescriptor(AccessibleObject.class))
				.setMaxs(3, 1);
		}
	}

	private static <T> MethodAccessor<T> generic(Method target, int kind)
	{
		Class<?> clazz = target.getDeclaringClass();
		boolean access = checkAccessible(clazz.getClassLoader());
		String className = access ? clazz.getPackage().getName().replace('.', '/').concat("/MethodAccessor") : "org/mve/invoke/MethodAccessor";
		String desc = MethodType.methodType(target.getReturnType(), target.getParameterTypes()).toMethodDescriptorString();
		final String owner = clazz.getTypeName().replace('.', '/');
		Class<?> returnType = target.getReturnType();
		Class<?>[] params = target.getParameterTypes();
		ClassWriter cw = new ClassWriter().addAttribute(new SourceWriter("MethodAccessor.java"));
		cw.set(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL | AccessFlag.ACC_SUPER, className, CONSTANT_POOL[0], new String[]{getType(MethodAccessor.class)});
		pregeneric(cw, target);
		boolean statics = Modifier.isStatic(target.getModifiers());
		boolean abstracts = Modifier.isAbstract(target.getModifiers());
		Consumer<CodeWriter> gen = code ->
		{
			final OperandStack stack = new OperandStack();
			if (!statics) arrayFirst(code, stack);
			pushArguments(params, code, statics ? 0 : 1, stack);
			int invoke = 0xB6 + kind;
			code.addMethodInstruction(invoke, owner, target.getName(), desc, abstracts);
			for (Class<?> c : params)
			{
				stack.pop();
				if (c == long.class || c == double.class) stack.pop();
			}
			if (!statics) stack.pop();
			if (returnType == void.class) { code.addInstruction(Opcodes.ACONST_NULL); stack.push(); }
			else warp(returnType, code, stack);
			code.addInstruction(Opcodes.ARETURN);
			code.setMaxs(stack.getMaxSize(), 2);
		};
		gen.accept(cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;")
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode());
		if (params.length == 0 && statics) gen.accept(cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "()Ljava/lang/Object;")
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode());
		byte[] classcode = cw.toByteArray();
		return (MethodAccessor<T>) UNSAFE.allocateInstance(UNSAFE.defineAnonymousClass(access ? clazz : ReflectionFactory.class, classcode, null));
	}

	private static <T> FieldAccessor<T> generic(Field target)
	{
		Class<?> clazz = target.getDeclaringClass();
		Class<?> type = target.getType();
		boolean acc = checkAccessible(clazz.getClassLoader());
		String fieldName = target.getName();
		String className = acc ? clazz.getPackage().getName().replace('.', '/').concat("/FieldAccessor") : "org/mve/invoke/FieldAccessor";
		String desc = getDescriptor(type);
		String owner = clazz.getTypeName().replace('.', '/');
		final OperandStack stack = new OperandStack();
		ClassWriter cw = new ClassWriter().addAttribute(new SourceWriter("FieldAccessor.java"));
		cw.set(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL | AccessFlag.ACC_SUPER, className, CONSTANT_POOL[0], new String[]{getType(FieldAccessor.class)});
		pregeneric(cw, target);
		boolean statics = Modifier.isStatic(target.getModifiers());
		boolean finals = Modifier.isFinal(target.getModifiers());
		CodeWriter code = cw.addMethod(AccessFlag.ACC_PUBLIC, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;")
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode();
		Marker marker = new Marker();
		code.addInstruction(Opcodes.ALOAD_1);
		stack.push();
		code.addJumpInstruction(Opcodes.IFNULL, marker);
		stack.pop();
		code.addInstruction(Opcodes.ALOAD_1);
		stack.push();
		code.addInstruction(Opcodes.ARRAYLENGTH);
		code.addInstruction(statics ? Opcodes.ICONST_1 : Opcodes.ICONST_2);
		stack.push();
		code.addJumpInstruction(Opcodes.IF_ICMPLT, marker);
		stack.pop();
		stack.pop();
		if (type.isPrimitive())
		{
			code.addInstruction(Opcodes.ALOAD_1);
			stack.push();
			code.addInstruction(statics ? Opcodes.ICONST_0 : Opcodes.ICONST_1);
			stack.push();
			code.addInstruction(Opcodes.AALOAD);
			stack.pop();
			code.addJumpInstruction(Opcodes.IFNULL, marker);
			stack.pop();
		}
		if (finals)
		{
			long offset = statics ? UNSAFE.staticFieldOffset(target) : UNSAFE.objectFieldOffset(target);
			code.addFieldInstruction(Opcodes.GETSTATIC, getType(ReflectionFactory.class), "UNSAFE", getDescriptor(Unsafe.class));
			stack.push();
			if (statics) { code.addConstantInstruction(Opcodes.LDC, new Type(clazz)); stack.push(); }
			else arrayFirst(code, stack);
			code.addConstantInstruction(Opcodes.LDC2_W, offset);
			stack.push();
			code.addInstruction(Opcodes.ALOAD_1);
			stack.push();
			code.addInstruction(statics ? Opcodes.ICONST_0 : Opcodes.ICONST_1);
			stack.push();
			code.addInstruction(Opcodes.AALOAD);
			stack.pop();
			if (type.isPrimitive())
			{
				unwarp(type, code, stack);
				if (type == long.class || type == double.class) stack.push();
				if (type == byte.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putByteVolatile", "(Ljava/lang/Object;JB)V", true);
				else if (type == short.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putShortVolatile", "(Ljava/lang/Object;JS)V", true);
				else if (type == int.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putIntVolatile", "(Ljava/lang/Object;JI)V", true);
				else if (type == long.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putLongVolatile", "(Ljava/lang/Object;JJ)V", true);
				else if (type == float.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putFloatVolatile", "(Ljava/lang/Object;JF)V", true);
				else if (type == double.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putDoubleVolatile", "(Ljava/lang/Object;JD)V", true);
				else if (type == boolean.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putBooleanVolatile", "(Ljava/lang/Object;JZ)V", true);
				else if (type == char.class) code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putCharVolatile", "(Ljava/lang/Object;JC)V", true);
			}
			else code.addMethodInstruction(Opcodes.INVOKEINTERFACE, getType(Unsafe.class), "putObjectVolatile", "(Ljava/lang/Object;JLjava/lang/Object;)V", true);
			stack.pop();
			stack.pop();
			stack.pop();
			stack.pop();
		}
		else
		{
			if (!statics) arrayFirst(code, stack);
			code.addInstruction(Opcodes.ALOAD_1);
			stack.push();
			code.addInstruction(statics ? Opcodes.ICONST_0 : Opcodes.ICONST_1);
			stack.push();
			code.addInstruction(Opcodes.AALOAD);
			stack.pop();
			if (type.isPrimitive())
			{
				unwarp(type, code, stack);
				if (type == long.class || type == double.class) stack.push();
			}
			code.addFieldInstruction(statics ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD, owner, fieldName, desc);
			stack.pop();
			if (!statics) stack.pop();
		}
		code.mark(marker);
		if (!statics) arrayFirst(code, stack);
		code.addFieldInstruction(statics ? Opcodes.GETSTATIC : Opcodes.GETFIELD, owner, fieldName, desc);
		if (statics) stack.push();
		if (type.isPrimitive())
		{
			warp(type, code, stack);
			if (type == long.class || type == double.class) stack.pop();
		}
		code.addInstruction(Opcodes.ARETURN);
		stack.pop();
		code.setMaxs(stack.getMaxSize(), 2);
		if (statics)
		{
			code = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "()Ljava/lang/Object;")
				.addAttribute(
					new RuntimeVisibleAnnotationsWriter()
						.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
						.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
						.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
				)
				.addCode();
			code.setMaxs((type == long.class || type == double.class) ? 2 : 1, 1);
			code.addFieldInstruction(Opcodes.GETSTATIC, owner, fieldName, desc);
			if (type.isPrimitive()) warp(type, code, new OperandStack());
			code.addInstruction(Opcodes.ARETURN);
		}

		byte[] classcode = cw.toByteArray();
		return (FieldAccessor<T>) UNSAFE.allocateInstance(UNSAFE.defineAnonymousClass(acc ? clazz : ReflectionFactory.class, classcode, null));
	}

	private static <T> ConstructorAccessor<T> generic(Constructor<?> target)
	{
		Class<?> clazz = target.getDeclaringClass();
		if (clazz == void.class || clazz.isPrimitive() || clazz.isArray()) throw new IllegalArgumentException("illegal type: "+clazz);
		boolean access = checkAccessible(clazz.getClassLoader());
		String className = access ? clazz.getPackage().getName().replace('.', '/').concat("/ConstructorAccessor") : "org/mve/invoke/ConstructorAccessor";
		String desc = MethodType.methodType(void.class, target.getParameterTypes()).toMethodDescriptorString();
		String owner = clazz.getTypeName().replace('.', '/');
		ClassWriter cw = new ClassWriter().addAttribute(new SourceWriter("ConstructorAccessor.java"));
		cw.set(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL | AccessFlag.ACC_SUPER, className, CONSTANT_POOL[0], new String[]{getType(ConstructorAccessor.class)});
		pregeneric(cw, target);
		Consumer<CodeWriter> gen = code ->
		{
			final OperandStack stack = new OperandStack();
			code.addTypeInstruction(Opcodes.NEW, owner);
			stack.push();
			code.addInstruction(Opcodes.DUP);
			stack.push();
			pushArguments(target.getParameterTypes(), code, 0, stack);
			code.addMethodInstruction(Opcodes.INVOKESPECIAL, owner, "<init>", desc, false);
			for (Class<?> c : target.getParameterTypes())
			{
				stack.pop();
				if (c == long.class || c == double.class) stack.pop();
			}
			stack.pop();
			code.addInstruction(Opcodes.ARETURN);
			code.setMaxs(stack.getMaxSize(), 2);
		};
		gen.accept(cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;")
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode());
		if (target.getParameterTypes().length == 0) gen.accept(cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "()Ljava/lang/Object;")
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode());
		byte[] classcode = cw.toByteArray();
		return (ConstructorAccessor<T>) UNSAFE.allocateInstance(UNSAFE.defineAnonymousClass(access ? clazz : ReflectionFactory.class, classcode, null));
	}

	private static <T> ReflectionAccessor<T> generic(Class<?> target)
	{
		if (typeWarp(target) == Void.class || target.isPrimitive() || target.isArray()) throw new IllegalArgumentException("illegal type: "+target);
		boolean access = checkAccessible(target.getClassLoader());
		String className = access ? target.getPackage().getName().replace('.', '/').concat("/Allocator") : "org/mve/invoke/Allocator";
		ClassWriter cw = new ClassWriter().addAttribute(new SourceWriter("Allocator.java"));
		cw.set(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL | AccessFlag.ACC_SUPER, className, CONSTANT_POOL[0], new String[]{getType(ReflectionAccessor.class)});
		CodeWriter code = cw.addMethod(AccessFlag.ACC_PUBLIC, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;")
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode();
		code.addTypeInstruction(Opcodes.NEW, getType(target));
		code.addInstruction(Opcodes.ARETURN);
		code.setMaxs(1, 2);
		cw.addMethod(AccessFlag.ACC_PUBLIC, "invoke", "()Ljava/lang/Object;")
			.addAttribute(
				new RuntimeVisibleAnnotationsWriter()
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[3]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[4]))
					.addAnnotation(new AnnotationWriter().set(CONSTANT_POOL[5]))
			)
			.addCode()
			.addTypeInstruction(Opcodes.NEW, getType(target))
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 1);
		byte[] classcode = cw.toByteArray();
		return (ReflectionAccessor<T>) UNSAFE.allocateInstance(UNSAFE.defineAnonymousClass(access ? target : ReflectionFactory.class, classcode, null));
	}

	private static boolean checkAccessible(ClassLoader loader)
	{
		return checkAccessible(loader, ReflectionFactory.class.getClassLoader());
	}

	private static boolean checkAccessible(ClassLoader c1, ClassLoader c2)
	{
		while (true)
		{
			if (c1 == c2) return true;
			if (c1 == null) break;
			c1 = c1.getParent();
		}
		return false;
	}

	private static void arrayFirst(CodeWriter code, OperandStack stack)
	{
		code.addInstruction(Opcodes.ALOAD_1);
		stack.push();
		code.addInstruction(Opcodes.ICONST_0);
		stack.push();
		code.addInstruction(Opcodes.AALOAD);
		stack.pop();
	}

	private static void pushArguments(Class<?>[] paramTypes, CodeWriter code, int start, OperandStack stack)
	{
		for (Class<?> c : paramTypes)
		{
			code.addInstruction(Opcodes.ALOAD_1);
			stack.push();
			code.addNumberInstruction(Opcodes.BIPUSH, start++);
			stack.push();
			code.addInstruction(Opcodes.AALOAD);
			stack.pop();
			if (c.isPrimitive())
			{
				unwarp(c, code, stack);
				if (c == long.class || c == double.class) stack.push();
			}
		}
	}

	private static void warp(Class<?> c, CodeWriter code, OperandStack stack)
	{
		if (c == byte.class) code.addMethodInstruction(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
		else if (c == short.class) code.addMethodInstruction(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
		else if (c == int.class) code.addMethodInstruction(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		else if (c == long.class) code.addMethodInstruction(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
		else if (c == float.class) code.addMethodInstruction(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
		else if (c == double.class) code.addMethodInstruction(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
		else if (c == boolean.class) code.addMethodInstruction(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
		else if (c == char.class) code.addMethodInstruction(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
		if (c == long.class || c == double.class) stack.pop();
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

	private static void unwarp(Class<?> c, CodeWriter code, OperandStack stack)
	{
		if (c == byte.class) code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "byteValue", "()B", false);
		else if (c == short.class) code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "shortValue", "()S", false);
		else if (c == int.class) code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
		else if (c == long.class) code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J", false);
		else if (c == float.class) code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", false);
		else if (c == double.class) code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", false);
		else if (c == boolean.class) code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
		else if (c == char.class) code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);

		if (c == long.class || c == double.class) stack.push();
	}

	private static String getType(Class<?> clazz)
	{
		if (clazz.isPrimitive()) throw new IllegalArgumentException();
		return clazz.isArray() ? getDescriptor(clazz) : clazz.getTypeName().replace('.', '/');
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

	private static int typeSize(Class<?> c)
	{
		if (c == void.class) return 0;
		else if (c == byte.class || c == short.class || c == int.class || c == float.class || c == boolean.class || c == char.class) return 1;
		else if (c == long.class || c == double.class) return 2;
		else return 1;
	}

	private static boolean integer(Class<?> type)
	{
		return type == byte.class || type == short.class || type == int.class || type == boolean.class || type == char.class;
	}

	private static int parameterSize(Class<?>[] params)
	{
		int size = 0;
		for (Class<?> c : params) size += typeSize(c);
		return size;
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
			int majorVersion = new DataInputStream(in).readUnsignedShort();
			in.close();

			/*
			 * MagicAccessorImpl
			 */
			String mai;
			{
				if (majorVersion <= 0X34) mai = "sun/reflect/MagicAccessorImpl";
				else mai = "jdk/internal/reflect/MagicAccessorImpl";
				CONSTANT_POOL[0] = "java/lang/MagicAccessFactory";
			}

			/*
			 * Hidden stack
			 */
			{
				CONSTANT_POOL[3] = majorVersion < 57 ? "Ljava/lang/invoke/LambdaForm$Hidden;" : "Ljdk/internal/vm/annotation/Hidden;";
				CONSTANT_POOL[4] = majorVersion == 0x34 ? "Ljava/lang/invoke/ForceInline;" : "Ljdk/internal/vm/annotation/ForceInline;";
				CONSTANT_POOL[5] = "Ljava/lang/invoke/LambdaForm$Compiled;";
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
						.addMethod(AccessFlag.ACC_PUBLIC, "<init>", "()V")
						.addCode()
						.addInstruction(Opcodes.ALOAD_0)
						.addMethodInstruction(Opcodes.INVOKESPECIAL, mai, "<init>", "()V", false)
						.addInstruction(Opcodes.RETURN)
						.getClassWriter()
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
					cw.set(52, AccessFlag.ACC_SUPER | AccessFlag.ACC_PUBLIC, "org/mve/invoke/MethodHandleInvoker", "java/lang/Object", new String[]{getType(ReflectionAccessor.class)});
					cw.addSignature("Ljava/lang/Object;L"+getType(ReflectionAccessor.class)+"<Ljava/lang/Class<*>;>;");
					/*
					 * MethodHandleInvoker();
					 */
					{
						CodeWriter code = cw.addMethod(AccessFlag.ACC_PUBLIC, "<init>", "()V").addCode();
						code.addInstruction(Opcodes.ALOAD_0);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
						code.addInstruction(Opcodes.RETURN);
						code.setMaxs(1, 1);
					}
					/*
					 * Object invoke(Object...);
					 */
					{
						CodeWriter code = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;").addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ICONST_0);
						code.addInstruction(Opcodes.AALOAD);
						code.addTypeInstruction(Opcodes.CHECKCAST, getType(MethodHandle.class));
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ICONST_1);
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ARRAYLENGTH);
						code.addMethodInstruction(Opcodes.INVOKESTATIC, Arrays.class.getTypeName().replace('.', '/'), "copyOfRange", MethodType.methodType(Object[].class, Object[].class, int.class, int.class).toMethodDescriptorString(), false);
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(MethodHandle.class), "invokeWithArguments", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(4, 2);
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
				Class<?> usfClass = Class.forName(majorVersion > 0x34 ? "jdk.internal.misc.Unsafe" : "sun.misc.Unsafe");
				String className = "org/mve/invoke/UnsafeWrapper";
				Class<?> clazz;
				try
				{
					clazz = Class.forName(className.replace('/', '.'));
				}
				catch (Throwable t)
				{
					ClassWriter cw = new ClassWriter();
					cw.set(0x34, 0x21, className, CONSTANT_POOL[0], new String[]{getType(Unsafe.class)});
					cw.addField(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_FINAL | AccessFlag.ACC_STATIC, "final", getDescriptor(usfClass));

					// implement methods
					{
						Consumer<ClassWriter> implement = (cw1) ->
						{
							{
								CodeWriter code = cw1.addMethod(AccessFlag.ACC_PUBLIC, "getJavaVMVersion", "()I").addCode();
								code.addNumberInstruction(Opcodes.BIPUSH, majorVersion);
								code.addInstruction(Opcodes.IRETURN);
								code.setMaxs(1, 1);
							}
							BiConsumer<String[], Class<?>[]> method = (name, arr) ->
							{
								String desc = MethodType.methodType(arr[0], Arrays.copyOfRange(arr, 1, arr.length)).toMethodDescriptorString();
								MethodWriter mw = cw1.addMethod(AccessFlag.ACC_PUBLIC, name[0], desc);
								if (name.length == 3) mw.addSignature(name[2]);
								CodeWriter code = mw.addCode();
								code.addFieldInstruction(Opcodes.GETSTATIC, className, "final", getDescriptor(usfClass));
								int size = 0;
								for (int i = 1; i < arr.length; i++)
								{
									size++;
									Class<?> type = arr[i];
									if (type == byte.class || type == short.class || type == int.class || type == boolean.class || type == char.class) code.addLocalVariableInstruction(Opcodes.ILOAD, size);
									else if (type == long.class) code.addLocalVariableInstruction(Opcodes.LLOAD, size);
									else if (type == float.class) code.addLocalVariableInstruction(Opcodes.FLOAD, size);
									else if (type == double.class) code.addLocalVariableInstruction(Opcodes.DLOAD, size);
									else code.addLocalVariableInstruction(Opcodes.ALOAD, size);
									if (type == double.class || type == long.class) size++;
								}
								code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(usfClass), name[1], desc, false);
								Class<?> c = arr[0];
								if (c == void.class) code.addInstruction(Opcodes.RETURN);
								else if (c == byte.class || c == short.class || c == int.class || c == char.class || c == boolean.class) code.addInstruction(Opcodes.IRETURN);
								else if (c == long.class) code.addInstruction(Opcodes.LRETURN);
								else if (c == float.class) code.addInstruction(Opcodes.FRETURN);
								else if (c == double.class) code.addInstruction(Opcodes.DRETURN);
								else code.addInstruction(Opcodes.ARETURN);
								code.setMaxs(size+1, size+1);
							};
							BiConsumer<String[], Class<?>[]> unsupported = (name, arr) ->
							{
								String desc = MethodType.methodType(arr[0], Arrays.copyOfRange(arr, 1, arr.length)).toMethodDescriptorString();
								MethodWriter mw = cw1.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, name[0], desc);
								if (name.length == 2) mw.addSignature(name[1]);
								CodeWriter code = mw.addCode();
								int size = arr.length;
								for (Class<?> c : arr) if (c == long.class || c == double.class) size++;
								code.addTypeInstruction(Opcodes.NEW, getType(UnsupportedOperationException.class));
								code.addInstruction(Opcodes.DUP);
								code.addConstantInstruction(Opcodes.LDC_W, "Method "+name[0]+desc+" is unsupported at JVM version "+majorVersion);
								code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(UnsupportedOperationException.class), "<init>", MethodType.methodType(void.class, String.class).toMethodDescriptorString(), false);
								code.addInstruction(Opcodes.ATHROW);
								code.setMaxs(3, size);
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
								Class<?>[] arr = new Class[]{type, Object.class, long.class, type};
								v35method.accept(new String[]{str + "Acquire", str + "Acquire"}, arr);
								v35method.accept(new String[]{str + "Release", str + "Acquire"}, arr);
							};
							BiConsumer<String, Class<?>> v35GetAndBitwise = (name, type) ->
							{
								Class<?>[] arr = new Class[]{type, Object.class, long.class, type};
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

							method.accept(new String[]{"getByte", "getByte"}, new Class[]{byte.class, long.class});
							method.accept(new String[]{"getByte", "getByte"}, new Class[]{byte.class, Object.class, long.class});
							method.accept(new String[]{"getByteVolatile", "getByteVolatile"}, new Class[]{byte.class, Object.class, long.class});
							method.accept(new String[]{"putByte", "putByte"}, new Class[]{void.class, long.class, byte.class});
							method.accept(new String[]{"putByte", "putByte"}, new Class[]{void.class, Object.class, long.class, byte.class});
							method.accept(new String[]{"putByteVolatile", "putByteVolatile"}, new Class[]{void.class, Object.class, long.class, byte.class});
							method.accept(new String[]{"getShort", "getShort"}, new Class[]{short.class, long.class});
							method.accept(new String[]{"getShort", "getShort"}, new Class[]{short.class, Object.class, long.class});
							method.accept(new String[]{"getShortVolatile", "getShortVolatile"}, new Class[]{short.class, Object.class, long.class});
							method.accept(new String[]{"putShort", "putShort"}, new Class[]{void.class, long.class, short.class});
							method.accept(new String[]{"putShort", "putShort"}, new Class[]{void.class, Object.class, long.class, short.class});
							method.accept(new String[]{"putShortVolatile", "putShortVolatile"}, new Class[]{void.class, Object.class, long.class, short.class});
							method.accept(new String[]{"getInt", "getInt"}, new Class[]{int.class, long.class});
							method.accept(new String[]{"getInt", "getInt"}, new Class[]{int.class, Object.class, long.class});
							method.accept(new String[]{"getIntVolatile", "getIntVolatile"}, new Class[]{int.class, Object.class, long.class});
							method.accept(new String[]{"putInt", "putInt"}, new Class[]{void.class, long.class, int.class});
							method.accept(new String[]{"putInt", "putInt"}, new Class[]{void.class, Object.class, long.class, int.class});
							method.accept(new String[]{"putIntVolatile", "putIntVolatile"}, new Class[]{void.class, Object.class, long.class, int.class});
							method.accept(new String[]{"getLong", "getLong"}, new Class[]{long.class, long.class});
							method.accept(new String[]{"getLong", "getLong"}, new Class[]{long.class, Object.class, long.class});
							method.accept(new String[]{"getLongVolatile", "getLongVolatile"}, new Class[]{long.class, Object.class, long.class});
							method.accept(new String[]{"putLong", "putLong"}, new Class[]{void.class, long.class, long.class});
							method.accept(new String[]{"putLong", "putLong"}, new Class[]{void.class, Object.class, long.class, long.class});
							method.accept(new String[]{"putLongVolatile", "putLongVolatile"}, new Class[]{void.class, Object.class, long.class, long.class});
							method.accept(new String[]{"getFloat", "getFloat"}, new Class[]{float.class, long.class});
							method.accept(new String[]{"getFloat", "getFloat"}, new Class[]{float.class, Object.class, long.class});
							method.accept(new String[]{"getFloatVolatile", "getFloatVolatile"}, new Class[]{float.class, Object.class, long.class});
							method.accept(new String[]{"putFloat", "putFloat"}, new Class[]{void.class, long.class, float.class});
							method.accept(new String[]{"putFloat", "putFloat"}, new Class[]{void.class, Object.class, long.class, float.class});
							method.accept(new String[]{"putFloatVolatile", "putFloatVolatile"}, new Class[]{void.class, Object.class, long.class, float.class});
							method.accept(new String[]{"getDouble", "getDouble"}, new Class[]{double.class, long.class});
							method.accept(new String[]{"getDouble", "getDouble"}, new Class[]{double.class, Object.class, long.class});
							method.accept(new String[]{"getDoubleVolatile", "getDoubleVolatile"}, new Class[]{double.class, Object.class, long.class});
							method.accept(new String[]{"putDouble", "putDouble"}, new Class[]{void.class, long.class, double.class});
							method.accept(new String[]{"putDouble", "putDouble"}, new Class[]{void.class, Object.class, long.class, double.class});
							method.accept(new String[]{"putDoubleVolatile", "putDoubleVolatile"}, new Class[]{void.class, Object.class, long.class, double.class});
							method.accept(new String[]{"getBoolean", "getBoolean"}, new Class[]{boolean.class, long.class});
							method.accept(new String[]{"getBoolean", "getBoolean"}, new Class[]{boolean.class, Object.class, long.class});
							method.accept(new String[]{"getBooleanVolatile", "getBooleanVolatile"}, new Class[]{boolean.class, Object.class, long.class});
							method.accept(new String[]{"putBoolean", "putBoolean"}, new Class[]{void.class, long.class, boolean.class});
							method.accept(new String[]{"putBoolean", "putBoolean"}, new Class[]{void.class, Object.class, long.class, boolean.class});
							method.accept(new String[]{"putBooleanVolatile", "putBooleanVolatile"}, new Class[]{void.class, Object.class, long.class, boolean.class});
							method.accept(new String[]{"getChar", "getChar"}, new Class[]{char.class, long.class});
							method.accept(new String[]{"getChar", "getChar"}, new Class[]{char.class, Object.class, long.class});
							method.accept(new String[]{"getCharVolatile", "getCharVolatile"}, new Class[]{char.class, Object.class, long.class});
							method.accept(new String[]{"putChar", "putChar"}, new Class[]{void.class, long.class, char.class});
							method.accept(new String[]{"putChar", "putChar"}, new Class[]{void.class, Object.class, long.class, char.class});
							method.accept(new String[]{"putCharVolatile", "putCharVolatile"}, new Class[]{void.class, Object.class, long.class, char.class});
							method.accept(new String[]{"getObject", majorVersion > 0x37 ? "getReference" : "getObject"}, new Class[]{Object.class, Object.class, long.class});
							method.accept(new String[]{"getReference", majorVersion > 0x37 ? "getReference" : "getObject"}, new Class[]{Object.class, Object.class, long.class});
							method.accept(new String[]{"getObjectVolatile", majorVersion > 0x37 ? "getReferenceVolatile" : "getObjectVolatile"}, new Class[]{Object.class, Object.class, long.class});
							method.accept(new String[]{"getReferenceVolatile", majorVersion > 0x37 ? "getReferenceVolatile" : "getObjectVolatile"}, new Class[]{Object.class, Object.class, long.class});
							method.accept(new String[]{"putObject", majorVersion > 0x37 ? "putReference" : "putObject"}, new Class[]{void.class, Object.class, long.class, Object.class});
							method.accept(new String[]{"putReference", majorVersion > 0x37 ? "putReference" : "putObject"}, new Class[]{void.class, Object.class, long.class, Object.class});
							method.accept(new String[]{"putObjectVolatile", majorVersion > 0x37 ? "putReferenceVolatile" : "putObjectVolatile"}, new Class[]{void.class, Object.class, long.class, Object.class});
							method.accept(new String[]{"putReferenceVolatile", majorVersion > 0x37 ? "putReferenceVolatile" : "putObjectVolatile"}, new Class[]{void.class, Object.class, long.class, Object.class});
							method.accept(new String[]{"getAddress", "getAddress"}, new Class[]{long.class, long.class});
							method.accept(new String[]{"putAddress", "putAddress"}, new Class[]{void.class, long.class});
							method.accept(new String[]{"allocateMemory", "allocateMemory"}, new Class[]{long.class, long.class});
							method.accept(new String[]{"reallocateMemory", "reallocateMemory"}, new Class[]{long.class, long.class, long.class});
							method.accept(new String[]{"setMemory", "setMemory"}, new Class[]{void.class, Object.class, long.class, long.class, byte.class});
							method.accept(new String[]{"setMemory", "setMemory"}, new Class[]{void.class, long.class, long.class, byte.class});
							method.accept(new String[]{"copyMemory", "copyMemory"}, new Class[]{void.class, Object.class, long.class, Object.class, long.class, long.class});
							method.accept(new String[]{"copyMemory", "copyMemory"}, new Class[]{void.class, long.class, long.class});
							method.accept(new String[]{"freeMemory", "freeMemory"}, new Class[]{void.class, long.class});
							method.accept(new String[]{"staticFieldOffset", "staticFieldOffset"}, new Class[]{long.class, Field.class});
							method.accept(new String[]{"objectFieldOffset", "objectFieldOffset"}, new Class[]{long.class, Field.class});
							method.accept(new String[]{"staticFieldBase", "staticFieldBase"}, new Class[]{Object.class, Field.class});
							method.accept(new String[]{"shouldBeInitialized", "shouldBeInitialized", "(Ljava/lang/Class<*>;)Z"}, new Class[]{boolean.class, Class.class});
							method.accept(new String[]{"ensureClassInitialized", "ensureClassInitialized", "(Ljava/lang/Class<*>;)V"}, new Class[]{void.class, Class.class});
							method.accept(new String[]{"arrayBaseOffset", "arrayBaseOffset", "(Ljava/lang/Class<*>;)I"}, new Class[]{int.class, Class.class});
							method.accept(new String[]{"arrayIndexScale", "arrayIndexScale", "(Ljava/lang/Class<*>;)I"}, new Class[]{int.class, Class.class});
							method.accept(new String[]{"addressSize", "addressSize"}, new Class[]{int.class});
							method.accept(new String[]{"pageSize", "pageSize"}, new Class[]{int.class});
							method.accept(new String[]{"defineClass", "defineClass", "(Ljava/lang/String;[BIILjava/lang/ClassLoader;Ljava/security/ProtectionDomain;)Ljava/lang/Class<*>;"}, new Class[]{Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class});
							method.accept(new String[]{"defineAnonymousClass", "defineAnonymousClass", "(Ljava/lang/Class<*>;[B[Ljava/lang/Object;)Ljava/lang/Class<*>;"}, new Class[]{Class.class, Class.class, byte[].class, Object[].class});
							method.accept(new String[]{"allocateInstance", "allocateInstance", "(Ljava/lang/Class<*>;)Ljava/lang/Object;"}, new Class[]{Object.class, Class.class});
							method.accept(new String[]{"throwException", "throwException"}, new Class[]{void.class, Throwable.class});
							method.accept(new String[]{"compareAndSwapInt", majorVersion > 0x35 ? "compareAndSetInt" : "compareAndSwapInt"}, new Class[]{boolean.class, Object.class, long.class, int.class, int.class});
							method.accept(new String[]{"compareAndSetInt", majorVersion > 0x35 ? "compareAndSetInt" : "compareAndSwapInt"}, new Class[]{boolean.class, Object.class, long.class, int.class, int.class});
							method.accept(new String[]{"compareAndSwapLong", majorVersion > 0x35 ? "compareAndSetLong" : "compareAndSwapLong"}, new Class[]{boolean.class, Object.class, long.class, long.class, long.class});
							method.accept(new String[]{"compareAndSetLong", majorVersion > 0x35 ? "compareAndSetLong" : "compareAndSwapLong"}, new Class[]{boolean.class, Object.class, long.class, long.class, long.class});
							method.accept(new String[]{"compareAndSwapObject", majorVersion > 0x35 ? majorVersion <= 0x37 ? "compareAndSetObject" : "compareAndSetReference" : "compareAndSwapObject"}, new Class[]{boolean.class, Object.class, long.class, Object.class, Object.class});
							method.accept(new String[]{"compareAndSetObject", majorVersion > 0x35 ? majorVersion <= 0x37 ? "compareAndSetObject" : "compareAndSetReference" : "compareAndSwapObject"}, new Class[]{boolean.class, Object.class, long.class, Object.class, Object.class});
							method.accept(new String[]{"compareAndSetReference", majorVersion > 0x35 ? majorVersion <= 0x37 ? "compareAndSetObject" : "compareAndSetReference" : "compareAndSwapObject"}, new Class[]{boolean.class, Object.class, long.class, Object.class, Object.class});
							method.accept(new String[]{"putOrderedInt", majorVersion > 0x34 ? "putIntRelease" : "putOrderedInt"}, new Class[]{void.class, Object.class, long.class, int.class});
							method.accept(new String[]{"putIntRelease", majorVersion > 0x34 ? "putIntRelease" : "putOrderedInt"}, new Class[]{void.class, Object.class, long.class, int.class});
							method.accept(new String[]{"putOrderedLong", majorVersion > 0x34 ? "putLongRelease" : "putOrderedLong"}, new Class[]{void.class, Object.class, long.class, long.class});
							method.accept(new String[]{"putLongRelease", majorVersion > 0x34 ? "putLongRelease" : "putOrderedLong"}, new Class[]{void.class, Object.class, long.class, long.class});
							method.accept(new String[]{"putOrderedObject", majorVersion != 0x34 ? majorVersion <= 0x37 ? "putObjectRelease" : "putReferenceRelease" : "putOrderedObject"}, new Class[]{void.class, Object.class, long.class, long.class});
							method.accept(new String[]{"putObjectRelease", majorVersion != 0x34 ? majorVersion <= 0x37 ? "putObjectRelease" : "putReferenceRelease" : "putOrderedObject"}, new Class[]{void.class, Object.class, long.class, long.class});
							method.accept(new String[]{"putReferenceRelease", majorVersion != 0x34 ? majorVersion <= 0x37 ? "putObjectRelease" : "putReferenceRelease" : "putOrderedObject"}, new Class[]{void.class, Object.class, long.class, long.class});
							method.accept(new String[]{"unpark", "unpark"}, new Class[]{void.class, Object.class});
							method.accept(new String[]{"park", "park"}, new Class[]{void.class, boolean.class, long.class});
							method.accept(new String[]{"getLoadAverage", "getLoadAverage"}, new Class[]{int.class, double[].class, int.class});
							method.accept(new String[]{"getAndAddInt", "getAndAddInt"}, new Class[]{int.class, Object.class, long.class, int.class});
							method.accept(new String[]{"getAndSetInt", "getAndSetInt"}, new Class[]{int.class, Object.class, long.class, int.class});
							method.accept(new String[]{"getAndAddLong", "getAndAddLong"}, new Class[]{long.class, Object.class, long.class, long.class});
							method.accept(new String[]{"getAndSetLong", "getAndSetLong"}, new Class[]{long.class, Object.class, long.class, long.class});
							method.accept(new String[]{"getAndSetObject", majorVersion > 0x37 ? "getAndSetReference" : "getAndSetObject"}, new Class[]{Object.class, Object.class, long.class, Object.class});
							method.accept(new String[]{"getAndSetReference", majorVersion > 0x37 ? "getAndSetReference" : "getAndSetObject"}, new Class[]{Object.class, Object.class, long.class, Object.class});
							method.accept(new String[]{"loadFence", "loadFence"}, new Class[]{void.class});
							method.accept(new String[]{"storeFence", "storeFence"}, new Class[]{void.class});
							method.accept(new String[]{"fullFence", "fullFence"}, new Class[]{void.class});
							Class<?>[] arr = new Class[]{void.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"loadLoadFence"}, arr);
							else method.accept(new String[]{"loadLoadFence", "loadLoadFence"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"storeStoreFence"}, arr);
							else method.accept(new String[]{"storeStoreFence", "storeStoreFence"}, arr);
							arr = new Class[]{boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"unalignedAccess"}, arr);
							else method.accept(new String[]{"unalignedAccess", "unalignedAccess"}, arr);
							arr = new Class[]{long.class, Object.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getAddress"}, arr);
							else method.accept(new String[]{"getAddress", "getAddress"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putAddress"}, arr);
							else method.accept(new String[]{"putAddress", "putAddress"}, arr);
							arr = new Class[]{Object.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getUncompressedObject"}, arr);
							else method.accept(new String[]{"getUncompressedObject", "getUncompressedObject"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, Object.class, long.class, long.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"copySwapMemory"}, arr);
							else method.accept(new String[]{"copySwapMemory", "copySwapMemory"}, arr);
							arr = new Class[]{void.class, long.class, long.class, long.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"copySwapMemory"}, arr);
							else method.accept(new String[]{"copySwapMemory", "copySwapMemory"}, arr);
							arr = new Class[]{Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"defineClass0", "(Ljava/lang/String;[BIILjava/lang/ClassLoader;Ljava/security/ProtectionDomain;)Ljava/lang/Class<*>;"}, arr);
							else method.accept(new String[]{"defineClass0", "defineClass0", "(Ljava/lang/String;[BIILjava/lang/ClassLoader;Ljava/security/ProtectionDomain;)Ljava/lang/Class<?>;"}, arr);
							arr = new Class[]{Object.class, Class.class, int.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"allocateUninitializedArray", "(Ljava/lang/Class<*>;I)Ljava/lang/Object;"}, arr);
							else method.accept(new String[]{"allocateUninitializedArray", "allocateUninitializedArray", "(Ljava/lang/Class<*>;I)Ljava/lang/Object;"}, arr);
							arr = new Class[]{boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"isBigEndian"}, arr);
							else method.accept(new String[]{"isBigEndian", "isBigEndian"}, arr);
							arr = new Class[]{byte.class, Object.class, long.class, byte.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndAddByte"}, arr);
							else method.accept(new String[]{"getAndAddByte", "getAndAddByte"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndSetByte"}, arr);
							else method.accept(new String[]{"getAndSetByte", "getAndSetByte"}, arr);
							arr = new Class[]{short.class, Object.class, long.class, short.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndAddShort"}, arr);
							else method.accept(new String[]{"getAndAddShort", "getAndAddShort"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndSetShort"}, arr);
							else method.accept(new String[]{"getAndSetShort", "getAndSetShort"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getObjectOpaque"}, new Class[]{Object.class, Object.class, long.class});
							else method.accept(new String[]{"getObjectOpaque", majorVersion > 0x37 ? "putReferenceOpaque" : "getObjectOpaque"}, new Class[]{Object.class, Object.class, long.class});
							arr = new Class[]{Object.class, Object.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getObjectAcquire"}, arr);
							else method.accept(new String[]{"getObjectAcquire", majorVersion > 0x37 ? "getReferenceAcquire" : "getObjectAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getReferenceAcquire"}, arr);
							else method.accept(new String[]{"getReferenceAcquire", majorVersion > 0x37 ? "getReferenceAcquire" : "getObjectAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getReferenceOpaque"}, arr);
							else method.accept(new String[]{"getReferenceOpaque", majorVersion > 0x37 ? "getReferenceOpaque" : "getObjectOpaque"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getBooleanOpaque"}, arr);
							else method.accept(new String[]{"getBooleanOpaque", "getBooleanOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getBooleanAcquire"}, arr);
							else method.accept(new String[]{"getBooleanAcquire", "getBooleanAcquire"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putBooleanOpaque"}, arr);
							else method.accept(new String[]{"putBooleanOpaque", "putBooleanOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putBooleanRelease"}, arr);
							else method.accept(new String[]{"putBooleanRelease", "putBooleanRelease"}, arr);
							arr = new Class[]{byte.class, Object.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getByteOpaque"}, arr);
							else method.accept(new String[]{"getByteOpaque", "getByteOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getByteAcquire"}, arr);
							else method.accept(new String[]{"getByteAcquire", "getByteAcquire"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, byte.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putByteOpaque"}, arr);
							else method.accept(new String[]{"putByteOpaque", "putByteOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putByteRelease"}, arr);
							else method.accept(new String[]{"putByteRelease", "putByteRelease"}, arr);
							arr = new Class[]{short.class, Object.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getShortOpaque"}, arr);
							else method.accept(new String[]{"getShortOpaque", "getShortOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getShortAcquire"}, arr);
							else method.accept(new String[]{"getShortAcquire", "getShortAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getShortUnaligned"}, arr);
							else method.accept(new String[]{"getShortUnaligned", "getShortUnaligned"}, arr);
							arr = new Class[]{short.class, Object.class, long.class, boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getShortUnaligned"}, arr);
							else method.accept(new String[]{"getShortUnaligned", "getShortUnaligned"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, short.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putShortOpaque"}, arr);
							else method.accept(new String[]{"putShortOpaque", "putShortOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putShortRelease"}, arr);
							else method.accept(new String[]{"putShortRelease", "putShortRelease"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putShortUnaligned"}, arr);
							else method.accept(new String[]{"putShortUnaligned", "putShortUnaligned"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, short.class, boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putShortUnaligned"}, arr);
							else method.accept(new String[]{"putShortUnaligned", "putShortUnaligned"}, arr);
							arr = new Class[]{char.class, Object.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getCharOpaque"}, arr);
							else method.accept(new String[]{"getCharOpaque", "getCharOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getCharAcquire"}, arr);
							else method.accept(new String[]{"getCharAcquire", "getCharAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getCharUnaligned"}, arr);
							else method.accept(new String[]{"getCharUnaligned", "getCharUnaligned"}, arr);
							arr = new Class[]{char.class, Object.class, long.class, boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getCharUnaligned"}, arr);
							else method.accept(new String[]{"getCharUnaligned", "getCharUnaligned"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, char.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putCharOpaque"}, arr);
							else method.accept(new String[]{"putCharOpaque", "putCharOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putCharRelease"}, arr);
							else method.accept(new String[]{"putCharRelease", "putCharRelease"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putCharUnaligned"}, arr);
							else method.accept(new String[]{"putCharUnaligned", "putCharUnaligned"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, char.class, boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putCharUnaligned"}, arr);
							else method.accept(new String[]{"putCharUnaligned", "putCharUnaligned"}, arr);
							arr = new Class[]{int.class, Object.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getIntOpaque"}, arr);
							else method.accept(new String[]{"getIntOpaque", "getIntOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getIntAcquire"}, arr);
							else method.accept(new String[]{"getIntAcquire", "getIntAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getIntUnaligned"}, arr);
							else method.accept(new String[]{"getIntUnaligned", "getIntUnaligned"}, arr);
							arr = new Class[]{int.class, Object.class, long.class, boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getIntUnaligned"}, arr);
							else method.accept(new String[]{"getIntUnaligned", "getIntUnaligned"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, int.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putIntOpaque"}, arr);
							else method.accept(new String[]{"putIntOpaque", "putIntOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putIntUnaligned"}, arr);
							else method.accept(new String[]{"putIntUnaligned", "putIntUnaligned"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, int.class, boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putIntUnaligned"}, arr);
							else method.accept(new String[]{"putIntUnaligned", "putIntUnaligned"}, arr);
							arr = new Class[]{long.class, Object.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getLongOpaque"}, arr);
							else method.accept(new String[]{"getLongOpaque", "getLongOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getLongAcquire"}, arr);
							else method.accept(new String[]{"getLongAcquire", "getLongAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getLongUnaligned"}, arr);
							else method.accept(new String[]{"getLongUnaligned", "getLongUnaligned"}, arr);
							arr = new Class[]{long.class, Object.class, long.class, boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getLongUnaligned"}, arr);
							else method.accept(new String[]{"getLongUnaligned", "getLongUnaligned"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putLongOpaque"}, arr);
							else method.accept(new String[]{"putLongOpaque", "putLongOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putLongUnaligned"}, arr);
							else method.accept(new String[]{"putLongUnaligned", "putLongUnaligned"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, long.class, boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putLongUnaligned"}, arr);
							else method.accept(new String[]{"putLongUnaligned", "putLongUnaligned"}, arr);
							arr = new Class[]{float.class, Object.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getFloatOpaque"}, arr);
							else method.accept(new String[]{"getFloatOpaque", "getFloatOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getFloatAcquire"}, arr);
							else method.accept(new String[]{"getFloatAcquire", "getFloatAcquire"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, float.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putFloatOpaque"}, arr);
							else method.accept(new String[]{"putFloatOpaque", "putFloatOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putFloatRelease"}, arr);
							else method.accept(new String[]{"putFloatRelease", "putFloatRelease"}, arr);
							arr = new Class[]{double.class, Object.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getDoubleOpaque"}, arr);
							else method.accept(new String[]{"getDoubleOpaque", "getDoubleOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getDoubleAcquire"}, arr);
							else method.accept(new String[]{"getDoubleAcquire", "getDoubleAcquire"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, Object.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putObjectOpaque"}, arr);
							else method.accept(new String[]{"putObjectOpaque", "putObjectOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putReferenceOpaque"}, arr);
							else method.accept(new String[]{"putReferenceOpaque", "putReferenceOpaque"}, arr);
							arr = new Class[]{void.class, Object.class, long.class, double.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putDoubleOpaque"}, arr);
							else method.accept(new String[]{"putDoubleOpaque", "putDoubleOpaque"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"putDoubleRelease"}, arr);
							else method.accept(new String[]{"putDoubleRelease", "putDoubleRelease"}, arr);
							arr = new Class[]{Object.class, Object.class, long.class, Object.class, Object.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeObject"}, arr);
							else method.accept(new String[]{"compareAndExchangeObject", majorVersion > 0x37 ? "compareAndExchangeReference" : "compareAndExchangeObject"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeReference"}, arr);
							else method.accept(new String[]{"compareAndExchangeReference", majorVersion > 0x37 ? "compareAndExchangeReference" : "compareAndExchangeObject"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeObjectAcquire"}, arr);
							else method.accept(new String[]{"compareAndExchangeObjectAcquire", majorVersion > 0x37 ? "compareAndExchangeReferenceAcquire" : "compareAndExchangeObjectAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeReferenceAcquire"}, arr);
							else method.accept(new String[]{"compareAndExchangeReferenceAcquire", majorVersion > 0x37 ? "compareAndExchangeReferenceAcquire" : "compareAndExchangeObjectAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeObjectRelease"}, arr);
							else method.accept(new String[]{"compareAndExchangeObjectRelease", majorVersion > 0x37 ? "compareAndExchangeReferenceRelease" : "compareAndExchangeObjectRelease"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeReferenceRelease"}, arr);
							else method.accept(new String[]{"compareAndExchangeReferenceRelease", majorVersion > 0x37 ? "compareAndExchangeReferenceRelease" : "compareAndExchangeObjectRelease"}, arr);
							arr = new Class[]{long.class, Object.class, long.class, long.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeLong"}, arr);
							else method.accept(new String[]{"compareAndExchangeLong", "compareAndExchangeLong"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeLongAcquire"}, arr);
							else method.accept(new String[]{"compareAndExchangeLongAcquire", "compareAndExchangeLongAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeLongRelease"}, arr);
							else method.accept(new String[]{"compareAndExchangeLongRelease", "compareAndExchangeLongRelease"}, arr);
							arr = new Class[]{int.class, Object.class, long.class, int.class, int.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeInt"}, arr);
							else method.accept(new String[]{"compareAndExchangeInt", "compareAndExchangeInt"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeIntAcquire"}, arr);
							else method.accept(new String[]{"compareAndExchangeIntAcquire", "compareAndExchangeIntAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeIntRelease"}, arr);
							else method.accept(new String[]{"compareAndExchangeIntRelease", "compareAndExchangeIntRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, byte.class, byte.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndSetByte"}, arr);
							else method.accept(new String[]{"compareAndSetByte", "compareAndSetByte"}, arr);
							arr = new Class[]{byte.class, Object.class, long.class, byte.class, byte.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeByte"}, arr);
							else method.accept(new String[]{"compareAndExchangeByte", "compareAndExchangeByte"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeByteAcquire"}, arr);
							else method.accept(new String[]{"compareAndExchangeByteAcquire", "compareAndExchangeByteAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeByteRelease"}, arr);
							else method.accept(new String[]{"compareAndExchangeByteRelease", "compareAndExchangeByteRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, short.class, short.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndSetShort"}, arr);
							else method.accept(new String[]{"compareAndSetShort", "compareAndSetShort"}, arr);
							arr = new Class[]{short.class, Object.class, long.class, short.class, short.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeShort"}, arr);
							else method.accept(new String[]{"compareAndExchangeShort", "compareAndExchangeShort"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeShortAcquire"}, arr);
							else method.accept(new String[]{"compareAndExchangeShortAcquire", "compareAndExchangeShortAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeShortRelease"}, arr);
							else method.accept(new String[]{"compareAndExchangeShortRelease", "compareAndExchangeShortRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, char.class, char.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndSetChar"}, arr);
							else method.accept(new String[]{"compareAndSetChar", "compareAndSetChar"}, arr);
							arr = new Class[]{char.class, Object.class, long.class, char.class, char.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeChar"}, arr);
							else method.accept(new String[]{"compareAndExchangeChar", "compareAndExchangeChar"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeCharAcquire"}, arr);
							else method.accept(new String[]{"compareAndExchangeCharAcquire", "compareAndExchangeCharAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeCharRelease"}, arr);
							else method.accept(new String[]{"compareAndExchangeCharRelease", "compareAndExchangeCharRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, float.class, float.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndSetFloat"}, arr);
							else method.accept(new String[]{"compareAndSetFloat", "compareAndSetFloat"}, arr);
							arr = new Class[]{float.class, Object.class, long.class, float.class, float.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeFloat"}, arr);
							else method.accept(new String[]{"compareAndExchangeFloat", "compareAndExchangeFloat"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeFloatAcquire"}, arr);
							else method.accept(new String[]{"compareAndExchangeFloatAcquire", "compareAndExchangeFloatAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeFloatRelease"}, arr);
							else method.accept(new String[]{"compareAndExchangeFloatRelease", "compareAndExchangeFloatRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, boolean.class, boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndSetBoolean"}, arr);
							else method.accept(new String[]{"compareAndSetBoolean", "compareAndSetBoolean"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeBoolean"}, arr);
							else method.accept(new String[]{"compareAndExchangeBoolean", "compareAndExchangeBoolean"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeBooleanAcquire"}, arr);
							else method.accept(new String[]{"compareAndExchangeBooleanAcquire", "compareAndExchangeBooleanAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeBooleanRelease"}, arr);
							else method.accept(new String[]{"compareAndExchangeBooleanRelease", "compareAndExchangeBooleanRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, double.class, double.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndSetDouble"}, arr);
							else method.accept(new String[]{"compareAndSetDouble", "compareAndSetDouble"}, arr);
							arr = new Class[]{double.class, Object.class, long.class, double.class, double.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeDouble"}, arr);
							else method.accept(new String[]{"compareAndExchangeDouble", "compareAndExchangeDouble"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeDoubleAcquire"}, arr);
							else method.accept(new String[]{"compareAndExchangeDoubleAcquire", "compareAndExchangeDoubleAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"compareAndExchangeDoubleRelease"}, arr);
							else method.accept(new String[]{"compareAndExchangeDoubleRelease", "compareAndExchangeDoubleRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, Object.class, Object.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetObject"}, arr);
							else method.accept(new String[]{"weakCompareAndSetObject", majorVersion > 0x37 ? "weakCompareAndSetReference" : "weakCompareAndSetObject"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetReference"}, arr);
							else method.accept(new String[]{"weakCompareAndSetReference", majorVersion > 0x37 ? "weakCompareAndSetReference" : "weakCompareAndSetObject"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetObjectPlain"}, arr);
							else method.accept(new String[]{"weakCompareAndSetObjectPlain", majorVersion > 0x37 ? "weakCompareAndSetReferencePlain" : "weakCompareAndSetObjectPlain"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetReferencePlain"}, arr);
							else method.accept(new String[]{"weakCompareAndSetReferencePlain", majorVersion > 0x37 ? "weakCompareAndSetReferencePlain" : "weakCompareAndSetObjectPlain"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetObjectAcquire"}, arr);
							else method.accept(new String[]{"weakCompareAndSetObjectAcquire", majorVersion > 0x37 ? "weakCompareAndSetReferenceAcquire" : "weakCompareAndSetObjectAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetReferenceAcquire"}, arr);
							else method.accept(new String[]{"weakCompareAndSetReferenceAcquire", majorVersion > 0x37 ? "weakCompareAndSetReferenceAcquire" : "weakCompareAndSetObjectAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetObjectRelease"}, arr);
							else method.accept(new String[]{"weakCompareAndSetObjectRelease", majorVersion > 0x37 ? "weakCompareAndSetReferenceRelease" : "weakCompareAndSetObjectRelease"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetReferenceRelease"}, arr);
							else method.accept(new String[]{"weakCompareAndSetReferenceRelease", majorVersion > 0x37 ? "weakCompareAndSetReferenceRelease" : "weakCompareAndSetObjectRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, byte.class, byte.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetByte"}, arr);
							else method.accept(new String[]{"weakCompareAndSetByte", "weakCompareAndSetByte"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetBytePlain"}, arr);
							else method.accept(new String[]{"weakCompareAndSetBytePlain", "weakCompareAndSetBytePlain"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetByteAcquire"}, arr);
							else method.accept(new String[]{"weakCompareAndSetByteAcquire", "weakCompareAndSetByteAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetByteRelease"}, arr);
							else method.accept(new String[]{"weakCompareAndSetByteRelease", "weakCompareAndSetByteRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, short.class, short.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetShort"}, arr);
							else method.accept(new String[]{"weakCompareAndSetShort", "weakCompareAndSetShort"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetShortPlain"}, arr);
							else method.accept(new String[]{"weakCompareAndSetShortPlain", "weakCompareAndSetShortPlain"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetShortAcquire"}, arr);
							else method.accept(new String[]{"weakCompareAndSetShortAcquire", "weakCompareAndSetShortAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetShortRelease"}, arr);
							else method.accept(new String[]{"weakCompareAndSetShortRelease", "weakCompareAndSetShortRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, int.class, int.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetInt"}, arr);
							else method.accept(new String[]{"weakCompareAndSetInt", "weakCompareAndSetInt"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetIntPlain"}, arr);
							else method.accept(new String[]{"weakCompareAndSetIntPlain", "weakCompareAndSetIntPlain"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetIntAcquire"}, arr);
							else method.accept(new String[]{"weakCompareAndSetIntAcquire", "weakCompareAndSetIntAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetIntRelease"}, arr);
							else method.accept(new String[]{"weakCompareAndSetIntRelease", "weakCompareAndSetIntRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, long.class, long.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetLong"}, arr);
							else method.accept(new String[]{"weakCompareAndSetLong", "weakCompareAndSetLong"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetLongPlain"}, arr);
							else method.accept(new String[]{"weakCompareAndSetLongPlain", "weakCompareAndSetLongPlain"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetLongAcquire"}, arr);
							else method.accept(new String[]{"weakCompareAndSetLongAcquire", "weakCompareAndSetLongAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetLongRelease"}, arr);
							else method.accept(new String[]{"weakCompareAndSetLongRelease", "weakCompareAndSetLongRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, float.class, float.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetFloat"}, arr);
							else method.accept(new String[]{"weakCompareAndSetFloat", "weakCompareAndSetFloat"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetFloatPlain"}, arr);
							else method.accept(new String[]{"weakCompareAndSetFloatPlain", "weakCompareAndSetFloatPlain"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetFloatAcquire"}, arr);
							else method.accept(new String[]{"weakCompareAndSetFloatAcquire", "weakCompareAndSetFloatAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetFloatRelease"}, arr);
							else method.accept(new String[]{"weakCompareAndSetFloatRelease", "weakCompareAndSetFloatRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, double.class, double.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetDouble"}, arr);
							else method.accept(new String[]{"weakCompareAndSetDouble", "weakCompareAndSetDouble"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetDoublePlain"}, arr);
							else method.accept(new String[]{"weakCompareAndSetDoublePlain", "weakCompareAndSetDoublePlain"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetDoubleAcquire"}, arr);
							else method.accept(new String[]{"weakCompareAndSetDoubleAcquire", "weakCompareAndSetDoubleAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetDoubleRelease"}, arr);
							else method.accept(new String[]{"weakCompareAndSetDoubleRelease", "weakCompareAndSetDoubleRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, boolean.class, boolean.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetBoolean"}, arr);
							else method.accept(new String[]{"weakCompareAndSetBoolean", "weakCompareAndSetBoolean"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetBooleanPlain"}, arr);
							else method.accept(new String[]{"weakCompareAndSetBooleanPlain", "weakCompareAndSetBooleanPlain"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetBooleanAcquire"}, arr);
							else method.accept(new String[]{"weakCompareAndSetBooleanAcquire", "weakCompareAndSetBooleanAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetBooleanRelease"}, arr);
							else method.accept(new String[]{"weakCompareAndSetBooleanRelease", "weakCompareAndSetBooleanRelease"}, arr);
							arr = new Class[]{boolean.class, Object.class, long.class, char.class, char.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetChar"}, arr);
							else method.accept(new String[]{"weakCompareAndSetChar", "weakCompareAndSetChar"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetCharPlain"}, arr);
							else method.accept(new String[]{"weakCompareAndSetCharPlain", "weakCompareAndSetCharPlain"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetCharAcquire"}, arr);
							else method.accept(new String[]{"weakCompareAndSetCharAcquire", "weakCompareAndSetCharAcquire"}, arr);
							if (majorVersion < 0x35) unsupported.accept(new String[]{"weakCompareAndSetCharRelease"}, arr);
							else method.accept(new String[]{"weakCompareAndSetCharRelease", "weakCompareAndSetCharRelease"}, arr);
							arr = new Class[]{char.class, Object.class, long.class, char.class};
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndAddChar"}, arr);
							else method.accept(new String[]{"getAndAddChar", "getAndAddChar"}, arr);
							arr[0] = arr[3] = float.class;
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndAddFloat"}, arr);
							else method.accept(new String[]{"getAndAddFloat", "getAndAddFloat"}, arr);
							arr[0] = arr[3] = double.class;
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndAddDouble"}, arr);
							else method.accept(new String[]{"getAndAddDouble", "getAndAddDouble"}, arr);
							arr[0] = arr[3] = boolean.class;
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndSetBoolean"}, arr);
							else method.accept(new String[]{"getAndSetBoolean", "getAndSetBoolean"}, arr);
							arr[0] = arr[3] = char.class;
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndSetChar"}, arr);
							else method.accept(new String[]{"getAndSetChar", "getAndSetChar"}, arr);
							arr[0] = arr[3] = float.class;
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndSetFloat"}, arr);
							else method.accept(new String[]{"getAndSetFloat", "getAndSetFloat"}, arr);
							arr[0] = arr[3] = double.class;
							if (majorVersion < 0x35) unsupported.accept(new String[]{"getAndSetDouble"}, arr);
							else method.accept(new String[]{"getAndSetDouble", "getAndSetDouble"}, arr);
							v35GetAndAddSetAR.accept("getAndAddByte", byte.class);
							v35GetAndAddSetAR.accept("getAndAddShort", short.class);
							v35GetAndAddSetAR.accept("getAndAddInt", int.class);
							v35GetAndAddSetAR.accept("getAndAddLong", long.class);
							v35GetAndAddSetAR.accept("getAndAddFloat", float.class);
							v35GetAndAddSetAR.accept("getAndAddDouble", double.class);
							v35GetAndAddSetAR.accept("getAndAddChar", char.class);
							arr[0] = arr[3] = Object.class;
							v35method.accept(new String[]{"getAndSetObjectAcquire", majorVersion > 0x37 ? "getAndSetReferenceAcquire" : "getAndSetObjectAcquire"}, arr);
							v35method.accept(new String[]{"getAndSetReferenceAcquire", majorVersion > 0x37 ? "getAndSetReferenceAcquire" : "getAndSetObjectAcquire"}, arr);
							v35method.accept(new String[]{"getAndSetObjectRelease", majorVersion > 0x37 ? "getAndSetReferenceRelease" : "getAndSetObjectRelease"}, arr);
							v35method.accept(new String[]{"getAndSetReferenceRelease", majorVersion > 0x37 ? "getAndSetReferenceRelease" : "getAndSetObjectRelease"}, arr);
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
							v36method.accept(new String[]{"objectFieldOffset", "objectFieldOffset", "(Ljava/lang/Class<*>;Ljava/lang/String;)J"}, new Class[]{long.class, Class.class, String.class});
						};
						implement.accept(cw);
					}

					// static constructor
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_STATIC, "<clinit>", "()V");
						CodeWriter code = mw.addCode();
						code.addFieldInstruction(Opcodes.GETSTATIC, getType(ReflectionFactory.class), "TRUSTED_LOOKUP", getDescriptor(MethodHandles.Lookup.class));
						code.addConstantInstruction(Opcodes.LDC, new Type(usfClass));
						code.addConstantInstruction(Opcodes.LDC_W, "theUnsafe");
						code.addConstantInstruction(Opcodes.LDC, new Type(usfClass));
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(MethodHandles.Lookup.class), "findStaticGetter", MethodType.methodType(MethodHandle.class, Class.class, String.class, Class.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ICONST_0);
						code.addTypeInstruction(Opcodes.ANEWARRAY, getType(Object.class));
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(MethodHandle.class), "invokeWithArguments", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false);
						code.addTypeInstruction(Opcodes.CHECKCAST, getType(usfClass));
						code.addFieldInstruction(Opcodes.PUTSTATIC, className, "final", getDescriptor(usfClass));
						code.addInstruction(Opcodes.RETURN);
						code.setMaxs(4, 0);
					}

					byte[] code = cw.toByteArray();
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
					String className = "org/mve/invoke/ReflectionMagicAccessor";
					ClassWriter cw = new ClassWriter();
					cw.set(0x34, AccessFlag.ACC_PUBLIC | AccessFlag.ACC_SUPER | AccessFlag.ACC_FINAL, className, CONSTANT_POOL[0], new String[]{getType(MagicAccessor.class)});
					cw.addSource("MagicAccessor.java");
					cw.addField(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_STATIC | AccessFlag.ACC_FINAL, "0", getDescriptor(SecurityManager.class));

					/*
					 * <clinit>
					 */
					{
						CodeWriter code = cw.addMethod(AccessFlag.ACC_STATIC, "<clinit>", "()V").addCode();
						code.addTypeInstruction(Opcodes.NEW, getType(SecurityManager.class));
						code.addInstruction(Opcodes.DUP);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(SecurityManager.class), "<init>", "()V", false);
						code.addFieldInstruction(Opcodes.PUTSTATIC, className, "0", getDescriptor(SecurityManager.class));
						code.addInstruction(Opcodes.RETURN);
						code.setMaxs(2, 0);
					}

					/*
					 * void setAccessible(AccessibleObject acc, boolean flag);
					 */
					{
						CodeWriter code = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "setAccessible", MethodType.methodType(void.class, AccessibleObject.class, boolean.class).toMethodDescriptorString()).addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ILOAD_2);
						code.addFieldInstruction(Opcodes.PUTFIELD, getType(AccessibleObject.class), "override", getDescriptor(boolean.class));
						code.addInstruction(Opcodes.RETURN);
						code.setMaxs(2, 3);
					}

					// Class<?> forName(String name);
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "forName", MethodType.methodType(Class.class, String.class).toMethodDescriptorString());
						mw.addSignature("(Ljava/lang/String;)Ljava/lang/Class<*>;");
						CodeWriter code = mw.addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ICONST_1);
						code.addInstruction(Opcodes.ALOAD_0);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, className, "getCallerClass", MethodType.methodType(Class.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.DUP);
						code.addFieldInstruction(Opcodes.GETFIELD, getType(Class.class), "classLoader", getDescriptor(ClassLoader.class));
						code.addInstruction(Opcodes.SWAP);
						code.addMethodInstruction(Opcodes.INVOKESTATIC, getType(Class.class), "forName0", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class, Class.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(4, 2);
					}

					// Class<?> forName(String name, boolean initialize, ClassLoader loader);
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "forName", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class).toMethodDescriptorString());
						mw.addSignature("(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class<*>;");
						CodeWriter code = mw.addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ILOAD_2);
						code.addInstruction(Opcodes.ALOAD_3);
						code.addInstruction(Opcodes.ACONST_NULL);
						code.addMethodInstruction(Opcodes.INVOKESTATIC, getType(Class.class), "forName0", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class, Class.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(4, 4);
					}

					// Class<?> defineClass(ClassLoader loader, byte[] code);
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "defineClass", MethodType.methodType(Class.class, ClassLoader.class, byte[].class).toMethodDescriptorString());
						mw.addSignature("(Ljava/lang/ClassLoader;[B)Ljava/lang/Class<*>;");
						CodeWriter code = mw.addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ACONST_NULL);
						code.addInstruction(Opcodes.ALOAD_2);
						code.addInstruction(Opcodes.ICONST_0);
						code.addInstruction(Opcodes.ALOAD_2);
						code.addInstruction(Opcodes.ARRAYLENGTH);
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(ClassLoader.class), "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(5, 3);
					}

					/*
					 * Class<?> getCallerClass();
					 */
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "getCallerClass", MethodType.methodType(Class.class).toMethodDescriptorString());
						mw.addSignature("()Ljava/lang/Class<*>;");
						CodeWriter code = mw.addCode();
						code.addFieldInstruction(Opcodes.GETSTATIC, className, "0", getDescriptor(SecurityManager.class));
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(SecurityManager.class), "getClassContext", MethodType.methodType(Class[].class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ICONST_2);
						code.addInstruction(Opcodes.AALOAD);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(2, 1);
					}

					/*
					 * Class<?>[] getClassContext();
					 */
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_FINAL | AccessFlag.ACC_PUBLIC, "getClassContext", MethodType.methodType(Class[].class).toMethodDescriptorString());
						mw.addSignature("()[Ljava/lang/Class<*>;");
						CodeWriter code = mw.addCode();
						code.addFieldInstruction(Opcodes.GETSTATIC, className, "0", getDescriptor(SecurityManager.class));
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(SecurityManager.class), "getClassContext", MethodType.methodType(Class[].class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.DUP);
						code.addInstruction(Opcodes.ARRAYLENGTH);
						code.addInstruction(Opcodes.ICONST_1);
						code.addInstruction(Opcodes.SWAP);
						code.addMethodInstruction(Opcodes.INVOKESTATIC, getType(Arrays.class), "copyOfRange", MethodType.methodType(Object[].class, Object[].class, int.class, int.class).toMethodDescriptorString(), false);
						code.addTypeInstruction(Opcodes.CHECKCAST, getType(Class[].class));
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(3, 1);
					}

					/*
					 * <T> T construct(Class<?> target);
					 */
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "construct", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString());
						mw.addSignature("<T:Ljava/lang/Object;>(Ljava/lang/Class<*>;)TT;");
						CodeWriter code = mw.addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ICONST_0);
						code.addTypeInstruction(Opcodes.ANEWARRAY, getType(Class.class));
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(Class.class), "getDeclaredConstructor", MethodType.methodType(Constructor.class, Class[].class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.DUP);
						code.addInstruction(Opcodes.ICONST_1);
						code.addFieldInstruction(Opcodes.PUTFIELD, getType(AccessibleObject.class), "override", getDescriptor(boolean.class));
						code.addInstruction(Opcodes.ICONST_0);
						code.addTypeInstruction(Opcodes.ANEWARRAY, getType(Object.class));
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(Constructor.class), "newInstance", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(3, 2);
					}

					/*
					 * <T> T construct(Class<?> target, Class<?>[] paramTypes, Object[] params);
					 */
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, "construct", MethodType.methodType(Object.class, Class.class, Class[].class, Object[].class).toMethodDescriptorString());
						mw.addSignature("<T:Ljava/lang/Object;>(Ljava/lang/Class<*>;Ljava/lang/Class<*>;[Ljava/lang/Object;)TT;");
						CodeWriter code = mw.addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ALOAD_2);
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(Class.class), "getDeclaredConstructor", MethodType.methodType(Constructor.class, Class[].class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.DUP);
						code.addInstruction(Opcodes.ICONST_1);
						code.addFieldInstruction(Opcodes.PUTFIELD, getType(AccessibleObject.class), "override", getDescriptor(boolean.class));
						code.addInstruction(Opcodes.ALOAD_3);
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(Constructor.class), "newInstance", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(3, 4);
					}

					/*
					 * Object invokeMethodHandle(MethodHandle handle, Object... args);
					 */
					{
						CodeWriter code = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "invokeMethodHandle", MethodType.methodType(Object.class, MethodHandle.class, Object[].class).toMethodDescriptorString()).addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ALOAD_2);
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(MethodHandle.class), "invokeWithArguments", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(2, 3);
					}

					/*
					 *Field getField(Class<?> target, String name);
					 */
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_PUBLIC, "getField", MethodType.methodType(Field.class, Class.class, String.class).toMethodDescriptorString());
						mw.addSignature("(Ljava/lang/Class<*>;Ljava/lang/String;)Ljava/lang/reflect/Field;");
						CodeWriter code = mw.addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ICONST_0);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Class.class), "getDeclaredFields0", MethodType.methodType(Field[].class, boolean.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ALOAD_2);
						code.addMethodInstruction(Opcodes.INVOKESTATIC, getType(Class.class), "searchFields", MethodType.methodType(Field.class, Field[].class, String.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.DUP);
						Marker marker = new Marker();
						code.addJumpInstruction(Opcodes.IFNONNULL, marker);
						code.addInstruction(Opcodes.POP);
						code.addTypeInstruction(Opcodes.NEW, getType(NoSuchFieldException.class));
						code.addInstruction(Opcodes.DUP);
						code.addInstruction(Opcodes.ALOAD_2);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(NoSuchFieldException.class), "<init>", MethodType.methodType(void.class, String.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ATHROW);
						code.mark(marker);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(3, 3);
					}
					/*
					 * Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes);
					 */
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "getMethod", "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
						mw.addSignature("(Ljava/lang/Class<*>;Ljava/lang/String;[Ljava/lang/Class<*>;)Ljava/lang/reflect/Method;");
						CodeWriter code = mw.addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ICONST_0);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Class.class), "getDeclaredMethods0", "(Z)[Ljava/lang/reflect/Method;", false);
						code.addInstruction(Opcodes.ALOAD_2);
						code.addInstruction(Opcodes.ALOAD_3);
						code.addMethodInstruction(Opcodes.INVOKESTATIC, getType(Class.class), "searchMethods", MethodType.methodType(Method.class, Method[].class, String.class, Class[].class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.DUP);
						Marker marker = new Marker();
						code.addJumpInstruction(Opcodes.IFNONNULL, marker);
						code.addInstruction(Opcodes.POP);
						code.addTypeInstruction(Opcodes.NEW, getType(NoSuchMethodException.class));
						code.addInstruction(Opcodes.DUP);
						code.addTypeInstruction(Opcodes.NEW, getType(StringBuilder.class));
						code.addInstruction(Opcodes.DUP);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(StringBuilder.class), "<init>", "()V", false);
						code.addInstruction(Opcodes.ALOAD_1);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Class.class), "getName", "()Ljava/lang/String;", false);
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
						code.addConstantInstruction(Opcodes.LDC, ".");
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ALOAD_2);
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ALOAD_3);
						code.addMethodInstruction(Opcodes.INVOKESTATIC, getType(Class.class), "argumentTypesToString", MethodType.methodType(String.class, Class[].class).toMethodDescriptorString(), false);
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(StringBuilder.class), "toString", "()Ljava/lang/String;", false);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(NoSuchMethodException.class), "<init>", "(Ljava/lang/String;)V", false);
						code.addInstruction(Opcodes.ATHROW);
						code.mark(marker);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(4, 4);
					}

					/*
					 * <T> Constructor<T> getConstructor(Class<?> target, Class<?> parameterTypes);
					 */
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_VARARGS, "getConstructor", MethodType.methodType(Constructor.class, Class.class, Class[].class).toMethodDescriptorString());
						mw.addSignature("<T:Ljava/lang/Object;>(Ljava/lang/Class<*>;[Ljava/lang/Class<*>;)Ljava/lang/reflect/Constructor<TT;>;");
						CodeWriter code = mw.addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ICONST_0);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Class.class), "getDeclaredConstructors0", MethodType.methodType(Constructor[].class, boolean.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.DUP);
						code.addInstruction(Opcodes.ASTORE_3);
						code.addInstruction(Opcodes.ARRAYLENGTH);
						code.addInstruction(Opcodes.ICONST_0);
						code.addLocalVariableInstruction(Opcodes.ISTORE, 4);
						Marker m1 = new Marker();
						code.mark(m1);
						code.addInstruction(Opcodes.DUP);
						code.addLocalVariableInstruction(Opcodes.ILOAD, 4);
						Marker m2 = new Marker();
						code.addJumpInstruction(Opcodes.IF_ICMPLE, m2);
						code.addInstruction(Opcodes.ALOAD_3);
						code.addLocalVariableInstruction(Opcodes.ILOAD, 4);
						code.addInstruction(Opcodes.AALOAD);
						code.addInstruction(Opcodes.DUP);
						code.addInstruction(Opcodes.ALOAD_2);
						code.addInstruction(Opcodes.SWAP);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Constructor.class), "getParameterTypes", MethodType.methodType(Class[].class).toMethodDescriptorString(), false);
						code.addMethodInstruction(Opcodes.INVOKESTATIC, getType(Class.class), "arrayContentsEq", MethodType.methodType(boolean.class, Object[].class, Object[].class).toMethodDescriptorString(), false);
						Marker m3 = new Marker();
						code.addJumpInstruction(Opcodes.IFEQ, m3);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Constructor.class), "copy", MethodType.methodType(Constructor.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.SWAP);
						code.addInstruction(Opcodes.POP);
						code.addInstruction(Opcodes.ARETURN);
						code.mark(m3);
						code.addInstruction(Opcodes.POP);
						code.addIincInstruction(4, 1);
						code.addJumpInstruction(Opcodes.GOTO, m1);
						code.mark(m2);
						code.addInstruction(Opcodes.POP);
						code.addTypeInstruction(Opcodes.NEW, getType(NoSuchMethodException.class));
						code.addInstruction(Opcodes.DUP);
						code.addTypeInstruction(Opcodes.NEW, getType(StringBuilder.class));
						code.addInstruction(Opcodes.DUP);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(StringBuilder.class), "<init>", "()V", false);
						code.addInstruction(Opcodes.ALOAD_1);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Class.class), "getName", "()Ljava/lang/String;", false);
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
						code.addConstantInstruction(Opcodes.LDC, ".<init>");
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ALOAD_2);
						code.addMethodInstruction(Opcodes.INVOKESTATIC, getType(Class.class), "argumentTypesToString", MethodType.methodType(String.class, Class[].class).toMethodDescriptorString(), false);
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
						code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, getType(StringBuilder.class), "toString", "()Ljava/lang/String;", false);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(NoSuchMethodException.class), "<init>", "(Ljava/lang/String;)V", false);
						code.addInstruction(Opcodes.ATHROW);
						code.setMaxs(4, 5);
					}

					/*
					 * Field[] getFields(Class<?>);
					 */
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_PUBLIC, "getFields", MethodType.methodType(Field[].class, Class.class).toMethodDescriptorString());
						mw.addSignature("(Ljava/lang/Class<*>;)[Ljava/lang/reflect/Field;");
						CodeWriter code = mw.addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ICONST_0);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Class.class), "getDeclaredFields0", MethodType.methodType(Field[].class, boolean.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(2, 2);
					}

					/*
					 * Method[] getMethods(Class<?>);
					 */
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_PUBLIC, "getMethods", MethodType.methodType(Method[].class, Class.class).toMethodDescriptorString());
						mw.addSignature("(Ljava/lang/Class<*>;)[Ljava/lang/reflect/Method;");
						CodeWriter code = mw.addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ICONST_0);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Class.class), "getDeclaredMethods0", MethodType.methodType(Method[].class, boolean.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(2, 2);
					}

					/*
					 * <T> Constructor<T>[] getConstructors(Class<?> target);
					 */
					{
						MethodWriter mw = cw.addMethod(AccessFlag.ACC_PUBLIC, "getConstructors", MethodType.methodType(Constructor[].class, Class.class).toMethodDescriptorString());
						mw.addSignature("<T:Ljava/lang/Object;>(Ljava/lang/Class<*>;)[Ljava/lang/reflect/Constructor<TT;>;");
						CodeWriter code = mw.addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ICONST_0);
						code.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Class.class), "getDeclaredConstructors0", MethodType.methodType(Constructor[].class, boolean.class).toMethodDescriptorString(), false);
						code.addInstruction(Opcodes.ARETURN);
						code.setMaxs(2, 2);
					}

					/*
					 * void throwException(Throwable t);
					 */
					{
						CodeWriter code = cw.addMethod(AccessFlag.ACC_PUBLIC, "throwException", "(Ljava/lang/Throwable;)V").addCode();
						code.addInstruction(Opcodes.ALOAD_1);
						code.addInstruction(Opcodes.ATHROW);
						code.setMaxs(1, 2);
					}

					/*
					 * void initialize(Object obj);
					 */
					{
						cw
							.addMethod(AccessFlag.ACC_PUBLIC, "initialize", MethodType.methodType(void.class, Object.class).toMethodDescriptorString())
							.addCode()
							.addInstruction(Opcodes.ALOAD_1)
							.addMethodInstruction(Opcodes.INVOKESPECIAL, getType(Object.class), "<init>", "()V", false)
							.addInstruction(Opcodes.RETURN)
							.setMaxs(1, 2);
					}

					byte[] code = cw.toByteArray();
					c = UNSAFE.defineClass(null, code, 0, code.length, ReflectionFactory.class.getClassLoader(), null);
				}
				ACCESSOR = (MagicAccessor) UNSAFE.allocateInstance(c);
			}

			/*
			 * Find enum cache
			 */
			{
				Field[] fields = ACCESSOR.getFields(Class.class);
				for (Field f : fields)
				{
					if (f.getType() == Object[].class && !Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
					{
						CONSTANT_POOL[1] = f.getName();
						break;
					}
				}
				for (Field f : fields)
				{
					if (f.getType() == Map.class)
					{
						CONSTANT_POOL[2] = f.getName();
						break;
					}
				}
			}
		}
		catch (Throwable t)
		{
			throw new UninitializedException(t);
		}
	}
}
