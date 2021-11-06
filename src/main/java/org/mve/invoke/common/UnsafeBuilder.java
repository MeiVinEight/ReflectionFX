package org.mve.invoke.common;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class UnsafeBuilder
{
	private static final String name = "org/mve/invoke/UnsafeWrapper";

	private final Lookup lookup;
	private final ClassWriter bytecode;
	private final MethodHandle define;
	private final MethodHandle anonymous;
	private final MethodHandle allocate;
	private final Map<String, Object> bridge = new HashMap<>();

	@SuppressWarnings("all")
	public UnsafeBuilder(Lookup lookup) throws Throwable
	{
		this.lookup = lookup;
		this.bytecode = new ClassWriter().set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, name, "java/lang/Object", new String[]{"org/mve/invoke/Unsafe"});

		Class<?> unsafeClass = Class.forName(JavaVM.VERSION > 0x34 ? "jdk.internal.misc.Unsafe" : "sun.misc.Unsafe");
		Object unsafe = this.lookup.findStaticGetter(unsafeClass, "theUnsafe", unsafeClass).invoke();

		this.define = this.lookup.findVirtual(unsafeClass, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class)).bindTo(unsafe);

		if (JavaVM.VERSION < Opcodes.version(17))
		{
			this.anonymous = this.lookup.findVirtual(unsafeClass, "defineAnonymousClass", MethodType.methodType(Class.class, Class.class, byte[].class, Object.class)).bindTo(unsafe);
		}
		else
		{
			this.anonymous = this.lookup.findStatic(ClassLoader.class, "defineClass0", MethodType.methodType(Class.class, ClassLoader.class, Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class, boolean.class, int.class, Object.class));
		}

		this.allocate = this.lookup.findVirtual(unsafeClass, "allocateInstance", MethodType.methodType(Object.class, Class.class)).bindTo(unsafe);
	}

	public byte[] build() throws Throwable
	{
		{
			{
				Class<?> finall = Class.forName(JavaVM.VERSION == 0x34 ? "sun.reflect.NativeMethodAccessorImpl" : "jdk.internal.reflect.NativeMethodAccessorImpl");
				ClassWriter abstractAccessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.INTERFACE | AccessFlag.ABSTRACT, Generator.name(), Generator.type(Object.class), null);
				ClassWriter accessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, Generator.name(), JavaVM.CONSTANT[0], new String[]{abstractAccessWriter.name});
				FieldWriter bridge = new FieldWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL, Generator.name(), abstractAccessWriter.name);
				this.bytecode.field(bridge);

				this.bridge(
					bridge,
					abstractAccessWriter,
					accessWriter,
					null,
					Generator.type(finall),
					"invoke0",
					"invoke0",
					new Class<?>[]{Object.class, Method.class, Object.class, Object[].class},
					false,
					false
				);

				byte[] code = abstractAccessWriter.toByteArray();
				this.define.invoke(null, code, 0, code.length, null, null);
				code = accessWriter.toByteArray();
				this.bridge.put(bridge.name, this.allocate.invoke(this.defineAnonymous(finall, code)));
			}

			{
				Class<?> finall = Class.forName(JavaVM.VERSION <= 0x34 ? "sun.reflect.NativeConstructorAccessorImpl" : "jdk.internal.reflect.NativeConstructorAccessorImpl");
				ClassWriter abstractAccessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.INTERFACE | AccessFlag.ABSTRACT, Generator.name(), Generator.type(Object.class), null);
				ClassWriter accessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, Generator.name(), JavaVM.CONSTANT[0], new String[]{abstractAccessWriter.name});
				FieldWriter bridge = new FieldWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL, Generator.name(), abstractAccessWriter.name);
				this.bytecode.field(bridge);

				this.bridge(
					bridge,
					abstractAccessWriter,
					accessWriter,
					null,
					Generator.type(finall),
					"newInstance0",
					"newInstance0",
					new Class<?>[]{Object.class, Constructor.class, Object[].class},
					false,
					false
				);

				byte[] code = abstractAccessWriter.toByteArray();
				this.define.invoke(null, code, 0, code.length, null, null);
				code = accessWriter.toByteArray();
				this.bridge.put(bridge.name, this.allocate.invoke(this.defineAnonymous(finall, code)));
			}

			{
				Class<?> finall = Class.forName(JavaVM.VERSION > 0x34 ? "jdk.internal.misc.Unsafe" : "sun.misc.Unsafe");
				String unsafeType = Generator.type(finall);
				ClassWriter abstractAccessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.INTERFACE | AccessFlag.ABSTRACT, Generator.name(), Generator.type(Object.class), null);
				ClassWriter accessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, Generator.name(), JavaVM.CONSTANT[0], new String[]{abstractAccessWriter.name});
				FieldWriter bridge = new FieldWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL, Generator.name(), abstractAccessWriter.name);
				FieldWriter access = new FieldWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL, Generator.name(), Generator.signature(finall));
				this.bytecode.field(bridge);
				accessWriter.field(access);

				Consumer<Object[]> unsafe = (o) -> this.bridge(bridge, abstractAccessWriter, accessWriter, access, unsafeType, (String) o[0], (String) o[1], (Class<?>[]) o[2], true, false);

				unsafe.accept(new Object[]{"getByte", "getByte", new Class[]{byte.class, long.class}});
				unsafe.accept(new Object[]{"getByte", "getByte", new Class[]{byte.class, Object.class, long.class}});
				unsafe.accept(new Object[]{"putByte", "putByte", new Class[]{void.class, long.class, byte.class}});
				unsafe.accept(new Object[]{"putByte", "putByte", new Class[]{void.class, Object.class, long.class, byte.class}});
				unsafe.accept(new Object[]{"getShort", "getShort", new Class[]{short.class, long.class}});
				unsafe.accept(new Object[]{"getShort", "getShort", new Class[]{short.class, Object.class, long.class}});
				unsafe.accept(new Object[]{"putShort", "putShort", new Class[]{void.class, long.class, short.class}});
				unsafe.accept(new Object[]{"putShort", "putShort", new Class[]{void.class, Object.class, long.class, short.class}});
				unsafe.accept(new Object[]{"getInt", "getInt", new Class[]{int.class, long.class}});
				unsafe.accept(new Object[]{"getInt", "getInt", new Class[]{int.class, Object.class, long.class}});
				unsafe.accept(new Object[]{"putInt", "putInt", new Class[]{void.class, long.class, int.class}});
				unsafe.accept(new Object[]{"putInt", "putInt", new Class[]{void.class, Object.class, long.class, int.class}});
				unsafe.accept(new Object[]{"getLong", "getLong", new Class[]{long.class, long.class}});
				unsafe.accept(new Object[]{"getLong", "getLong", new Class[]{long.class, Object.class, long.class}});
				unsafe.accept(new Object[]{"putLong", "putLong", new Class[]{void.class, long.class, long.class}});
				unsafe.accept(new Object[]{"putLong", "putLong", new Class[]{void.class, Object.class, long.class, long.class}});
				unsafe.accept(new Object[]{"getFloat", "getFloat", new Class[]{float.class, long.class}});
				unsafe.accept(new Object[]{"getFloat", "getFloat", new Class[]{float.class, Object.class, long.class}});
				unsafe.accept(new Object[]{"putFloat", "putFloat", new Class[]{void.class, long.class, float.class}});
				unsafe.accept(new Object[]{"putFloat", "putFloat", new Class[]{void.class, Object.class, long.class, float.class}});
				unsafe.accept(new Object[]{"getDouble", "getDouble", new Class[]{double.class, long.class}});
				unsafe.accept(new Object[]{"getDouble", "getDouble", new Class[]{double.class, Object.class, long.class}});
				unsafe.accept(new Object[]{"putDouble", "putDouble", new Class[]{void.class, long.class, double.class}});
				unsafe.accept(new Object[]{"putDouble", "putDouble", new Class[]{void.class, Object.class, long.class, double.class}});
				unsafe.accept(new Object[]{"getBoolean", "getBoolean", new Class[]{boolean.class, long.class}});
				unsafe.accept(new Object[]{"getBoolean", "getBoolean", new Class[]{boolean.class, Object.class, long.class}});
				unsafe.accept(new Object[]{"putBoolean", "putBoolean", new Class[]{void.class, long.class, boolean.class}});
				unsafe.accept(new Object[]{"putBoolean", "putBoolean", new Class[]{void.class, Object.class, long.class, boolean.class}});
				unsafe.accept(new Object[]{"getChar", "getChar", new Class[]{char.class, long.class}});
				unsafe.accept(new Object[]{"getChar", "getChar", new Class[]{char.class, Object.class, long.class}});
				unsafe.accept(new Object[]{"putChar", "putChar", new Class[]{void.class, long.class, char.class}});
				unsafe.accept(new Object[]{"putChar", "putChar", new Class[]{void.class, Object.class, long.class, char.class}});
				unsafe.accept(new Object[]{"getObject", JavaVM.VERSION > 0x37 ? "getReference" : "getObject", new Class[]{Object.class, Object.class, long.class}});
				unsafe.accept(new Object[]{"putObject", JavaVM.VERSION > 0x37 ? "putReference" : "putObject", new Class[]{void.class, Object.class, long.class, Object.class}});
				unsafe.accept(new Object[]{"getAddress", "getAddress", new Class[]{long.class, long.class}});
				unsafe.accept(new Object[]{"putAddress", "putAddress", new Class[]{void.class, long.class}});
				unsafe.accept(new Object[]{"allocateMemory", "allocateMemory", new Class[]{long.class, long.class}});
				unsafe.accept(new Object[]{"reallocateMemory", "reallocateMemory", new Class[]{long.class, long.class, long.class}});
				unsafe.accept(new Object[]{"setMemory", "setMemory", new Class[]{void.class, Object.class, long.class, long.class, byte.class}});
				unsafe.accept(new Object[]{"setMemory", "setMemory", new Class[]{void.class, long.class, long.class, byte.class}});
				unsafe.accept(new Object[]{"copyMemory", "copyMemory", new Class[]{void.class, Object.class, long.class, Object.class, long.class, long.class}});
				unsafe.accept(new Object[]{"copyMemory", "copyMemory", new Class[]{void.class, long.class, long.class}});
				unsafe.accept(new Object[]{"freeMemory", "freeMemory", new Class[]{void.class, long.class}});
				unsafe.accept(new Object[]{"staticFieldOffset", "staticFieldOffset", new Class[]{long.class, Field.class}});
				unsafe.accept(new Object[]{"objectFieldOffset", "objectFieldOffset", new Class[]{long.class, Field.class}});
				unsafe.accept(new Object[]{"staticFieldBase", "staticFieldBase", new Class[]{Object.class, Field.class}});
				unsafe.accept(new Object[]{"shouldBeInitialized", "shouldBeInitialized", new Class[]{boolean.class, Class.class}});
				unsafe.accept(new Object[]{"ensureClassInitialized", "ensureClassInitialized", new Class[]{void.class, Class.class}});
				unsafe.accept(new Object[]{"arrayBaseOffset", "arrayBaseOffset", new Class[]{int.class, Class.class}});
				unsafe.accept(new Object[]{"arrayIndexScale", "arrayIndexScale", new Class[]{int.class, Class.class}});
				unsafe.accept(new Object[]{"addressSize", "addressSize", new Class[]{int.class}});
				unsafe.accept(new Object[]{"pageSize", "pageSize", new Class[]{int.class}});
				unsafe.accept(new Object[]{"defineClass", "defineClass", new Class[]{Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class}});
				if (JavaVM.VERSION < Opcodes.version(17))
				{
					unsafe.accept(new Object[]{"defineAnonymousClass", "defineAnonymousClass", new Class[]{Class.class, Class.class, byte[].class, Object[].class}});
				}
				unsafe.accept(new Object[]{"allocateInstance", "allocateInstance", new Class[]{Object.class, Class.class}});
				unsafe.accept(new Object[]{"throwException", "throwException", new Class[]{void.class, Throwable.class}});
				unsafe.accept(new Object[]{"compareAndSwapInt", JavaVM.VERSION > 0x35 ? "compareAndSetInt" : "compareAndSwapInt", new Class[]{boolean.class, Object.class, long.class, int.class, int.class}});
				unsafe.accept(new Object[]{"compareAndSwapLong", JavaVM.VERSION > 0x35 ? "compareAndSetLong" : "compareAndSwapLong", new Class[]{boolean.class, Object.class, long.class, long.class, long.class}});
				unsafe.accept(new Object[]{"compareAndSwapObject", JavaVM.VERSION > 0x35 ? JavaVM.VERSION <= 0x37 ? "compareAndSetObject" : "compareAndSetReference" : "compareAndSwapObject", new Class[]{boolean.class, Object.class, long.class, Object.class, Object.class}});
				unsafe.accept(new Object[]{"unpark", "unpark", new Class[]{void.class, Object.class}});
				unsafe.accept(new Object[]{"park", "park", new Class[]{void.class, boolean.class, long.class}});
				unsafe.accept(new Object[]{"loadFence", "loadFence", new Class[]{void.class}});
				unsafe.accept(new Object[]{"storeFence", "storeFence", new Class[]{void.class}});
				unsafe.accept(new Object[]{"fullFence", "fullFence", new Class[]{void.class}});

				// static constructor
				this.bytecode.method(new MethodWriter()
					.set(AccessFlag.STATIC, "<clinit>", "()V")
					.attribute(new CodeWriter()
						.field(Opcodes.GETSTATIC, unsafeType, "theUnsafe", Generator.signature(finall))
						.field(Opcodes.PUTSTATIC, accessWriter.name, access.name, Generator.signature(finall))
						.max(1, 0)
					)
				);

				byte[] code = abstractAccessWriter.toByteArray();
				this.define.invoke(null, code, 0, code.length, null, null);
				code = accessWriter.toByteArray();
				this.bridge.put(bridge.name, this.allocate.invoke(this.defineAnonymous(finall, code)));
			}

			if (JavaVM.VERSION > Opcodes.version(16))
			{
				Class<?> finall = ClassLoader.class;
				ClassWriter abstractAccessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.INTERFACE | AccessFlag.ABSTRACT, Generator.name(), Generator.type(Object.class), null);
				ClassWriter accessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, Generator.name(), JavaVM.CONSTANT[0], new String[]{abstractAccessWriter.name});
				FieldWriter bridge = new FieldWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL, Generator.name(), abstractAccessWriter.name);
				this.bytecode.field(bridge);

				accessWriter.method(new MethodWriter()
					.set(AccessFlag.PUBLIC, "defineAnonymousClass", MethodType.methodType(Class.class, Class.class, byte[].class, Object[].class).toMethodDescriptorString())
					.attribute(new CodeWriter()
						.instruction(Opcodes.ALOAD_1)
						.method(Opcodes.INVOKEVIRTUAL, Generator.type(Class.class), "getClassLoader", MethodType.methodType(ClassLoader.class).toMethodDescriptorString(), false)
						.instruction(Opcodes.ALOAD_1)
						.instruction(Opcodes.ACONST_NULL)
						.instruction(Opcodes.ALOAD_2)
						.instruction(Opcodes.ICONST_0)
						.instruction(Opcodes.ALOAD_2)
						.instruction(Opcodes.ARRAYLENGTH)
						.instruction(Opcodes.ACONST_NULL)
						.instruction(Opcodes.ICONST_1)
						.number(Opcodes.BIPUSH, 11)
						.instruction(Opcodes.ALOAD_3)
						.method(Opcodes.INVOKESTATIC, Generator.type(ClassLoader.class), "defineClass0", MethodType.methodType(Class.class, ClassLoader.class, Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class, boolean.class, int.class, Object.class).toMethodDescriptorString(), false)
						.instruction(Opcodes.ARETURN)
						.max(10, 4)
					)
				);

				this.implement(this.bytecode, bridge, "defineAnonymousClass", abstractAccessWriter.name, "defineAnonymousClass", new Class<?>[]{Class.class, Class.class, byte[].class, Object[].class}, true, true);

				byte[] code = abstractAccessWriter.toByteArray();
				this.define.invoke(null, code, 0, code.length, null, null);
				code = accessWriter.toByteArray();
				this.bridge.put(bridge.name, this.allocate.invoke(this.defineAnonymous(finall, code)));
			}
		}

		return this.bytecode.toByteArray();
	}

	public void post(Class<?> c) throws Throwable
	{
		Class<?> unsafeClass = Class.forName(JavaVM.VERSION > 0x34 ? "jdk.internal.misc.Unsafe" : "sun.misc.Unsafe");
		Object unsafe = this.lookup.findStaticGetter(unsafeClass, "theUnsafe", unsafeClass).invoke();

		MethodHandle staticFieldOffset = this.lookup.findVirtual(unsafeClass, "staticFieldOffset", MethodType.methodType(long.class, Field.class)).bindTo(unsafe);
		MethodHandle putObject = this.lookup.findVirtual(unsafeClass, JavaVM.VERSION > 0x37 ? "putReference" : "putObject", MethodType.methodType(void.class, Object.class, long.class, Object.class)).bindTo(unsafe);

		for (Map.Entry<String, Object> entry : this.bridge.entrySet())
		{
			putObject.invoke(c, staticFieldOffset.invoke(c.getDeclaredField(entry.getKey())), entry.getValue());
		}
	}

	private Class<?> defineAnonymous(Class<?> host, byte[] code) throws Throwable
	{
		if (JavaVM.VERSION < Opcodes.version(17))
		{
			return (Class<?>) this.anonymous.invoke(host, code, null);
		}
		else
		{
			return (Class<?>) this.anonymous.invoke(host.getClassLoader(), host, null, code, 0, code.length, null, true, 11, null);
		}
	}

	private void bridge(FieldWriter bridge, ClassWriter with, ClassWriter instance, FieldWriter objective, String finall, String from, String into, Class<?>[] argument, boolean nonstatic, boolean abstracts)
	{
		this.implementWithAbstract(with, instance, objective, from, finall, into, argument, nonstatic, abstracts);
		this.implement(this.bytecode, bridge, from, with.name, from, argument, true, true);
	}

	private void implementWithAbstract(ClassWriter with, ClassWriter bytecode, FieldWriter objective, String from, String finall, String into, Class<?>[] argument, boolean nonstatic, boolean abstracts)
	{
		with.method(new MethodWriter().set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, from, MethodType.methodType(argument[0], Arrays.copyOfRange(argument, 1, argument.length)).toMethodDescriptorString()));
		this.implement(bytecode, objective, from, finall, into, argument, nonstatic, abstracts);
	}

	private void implement(ClassWriter bytecode, FieldWriter objective, String from, String finall, String into, Class<?>[] argument, boolean nonstatic, boolean abstracts)
	{
		MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, from, MethodType.methodType(argument[0], Arrays.copyOfRange(argument, 1, argument.length)).toMethodDescriptorString());
		CodeWriter cw = new CodeWriter();

		int stack = 0;
		if (nonstatic)
		{
			cw.field(Opcodes.GETSTATIC, bytecode.name, objective.name, objective.type);
			stack++;
		}
		for (int i = 1; i < argument.length; i++)
		{
			Generator.load(argument[i], cw, i);
			stack += Generator.typeSize(argument[i]);
		}
		cw.method(nonstatic ? (abstracts ? Opcodes.INVOKEINTERFACE :Opcodes.INVOKEVIRTUAL) : Opcodes.INVOKESTATIC, finall, into, MethodType.methodType(argument[0], Arrays.copyOfRange(argument, 1, argument.length)).toMethodDescriptorString(), abstracts);
		Generator.returner(argument[0], cw);
		cw.max(Math.max(stack, Generator.typeSize(argument[0])), stack);

		mw.attribute(cw);
		bytecode.method(mw);
	}
}
