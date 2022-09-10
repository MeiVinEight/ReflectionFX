package org.mve.invoke.common;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.attribute.StackMapTableWriter;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.attribute.code.stack.verification.Verification;
import org.mve.invoke.MethodKind;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UnsafeBuilder
{
	private static final String name = "org/mve/invoke/UnsafeWrapper";

	private final Lookup lookup;
	private final ClassWriter bytecode;
	private final MethodHandle define;
	private final MethodHandle anonymous;
	private final MethodHandle allocate;
	private final MethodHandle module;
	private final MethodHandle read;
	private final Map<String, Object> bridge = new HashMap<>();
	private final ClassLoader bootstrap;

	@SuppressWarnings("all")
	public UnsafeBuilder(Lookup lookup) throws Throwable
	{
		{
			ClassLoader bscl = null;
			{
				try
				{
					bscl = (ClassLoader) lookup.findStaticGetter(
						ClassLoader.class,
						"bootstrapClassLoader",
						ClassLoader.class
					).invoke();
				}
				catch (NoSuchFieldException ignored)
				{
				}
			}
			this.bootstrap = bscl;
		}
		this.lookup = lookup;
		this.bytecode = new ClassWriter().set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, name, "java/lang/Object", new String[]{"org/mve/invoke/Unsafe"});

		Class<?> unsafeClass = Class.forName(JavaVM.VERSION > 0x34 ? "jdk.internal.misc.Unsafe" : "sun.misc.Unsafe");
		Object unsafe = this.lookup.findStaticGetter(unsafeClass, "theUnsafe", unsafeClass).invoke();

		this.define = this.lookup.findVirtual(unsafeClass, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class)).bindTo(unsafe);

		if (JavaVM.VERSION < Opcodes.version(17))
		{
			this.anonymous = this.lookup.findVirtual(unsafeClass, "defineAnonymousClass", MethodType.methodType(Class.class, Class.class, byte[].class, Object[].class)).bindTo(unsafe);
		}
		else
		{
			if (this.bootstrap != null)
			{
				this.anonymous = this.lookup.findVirtual(
					ClassLoader.class,
					"defineClassInternal",
					MethodType.methodType(
						Class.class,
						Class.class,
						String.class,
						byte[].class,
						ProtectionDomain.class,
						boolean.class,
						int.class,
						Object.class
					)
				);
			}
			else
			{
				this.anonymous = this.lookup.findStatic(
					ClassLoader.class,
					"defineClass0",
					MethodType.methodType(
						Class.class,
						ClassLoader.class,
						Class.class,
						String.class,
						byte[].class,
						int.class,
						int.class,
						ProtectionDomain.class,
						boolean.class,
						int.class,
						Object.class
					)
				);
			}
		}

		this.allocate = this.lookup.findVirtual(unsafeClass, "allocateInstance", MethodType.methodType(Object.class, Class.class)).bindTo(unsafe);
		MethodHandle module = null;
		MethodHandle read = null;
		if (JavaVM.VERSION > Opcodes.version(8))
		{
			module = this.lookup.findGetter(Class.class, "module", Class.forName("java.lang.Module"));
			read = this.lookup.findVirtual(Class.forName("java.lang.Module"), "implAddReads", MethodType.methodType(void.class, Class.forName("java.lang.Module"), boolean.class));
		}
		this.module = module;
		this.read = read;
	}

	public byte[] build() throws Throwable
	{
		{
			{
				Class<?> finall = Class.forName(JavaVM.VERSION == 0x34 ? "sun.reflect.NativeMethodAccessorImpl" : "jdk.internal.reflect.NativeMethodAccessorImpl");
				ClassWriter abstractAccessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.INTERFACE | AccessFlag.ABSTRACT, JavaVM.random(), Generator.type(Object.class), null);
				ClassWriter accessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, JavaVM.randomAnonymous(finall), JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC], new String[]{abstractAccessWriter.name});
				FieldWriter bridge = new FieldWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL, JavaVM.random(), "L" + abstractAccessWriter.name + ";");
				this.bytecode.field(bridge);

				String name = JavaVM.random();

				abstractAccessWriter.method(new MethodWriter()
					.set(
						AccessFlag.PUBLIC | AccessFlag.ABSTRACT,
						name,
						MethodType
							.methodType(
								Object.class,
								Method.class,
								Object.class,
								Object[].class
							)
							.toMethodDescriptorString()
					)
				);

				accessWriter.method(new MethodWriter()
					.set(
						AccessFlag.PUBLIC,
						name,
						MethodType
							.methodType(
								Object.class,
								Method.class,
								Object.class,
								Object[].class
							)
							.toMethodDescriptorString()
					)
					.attribute(new CodeWriter()
						.instruction(Opcodes.ALOAD_1)
						.instruction(Opcodes.ALOAD_2)
						.instruction(Opcodes.ALOAD_3)
						.method(
							Opcodes.INVOKESTATIC,
							Generator.type(finall),
							"invoke0",
							MethodType
								.methodType(
									Object.class,
									Method.class,
									Object.class,
									Object[].class
								)
								.toMethodDescriptorString(),
							false
						)
						.instruction(Opcodes.ARETURN)
						.max(3, 4)
					)
				);

				this.bytecode.method(new MethodWriter()
					.set(
						AccessFlag.PUBLIC,
						"invoke",
						MethodType
							.methodType(
								Object.class,
								Method.class,
								Object.class,
								Object[].class
							)
							.toMethodDescriptorString()
					)
					.attribute(new CodeWriter()
						.field(Opcodes.GETSTATIC, this.bytecode.name, bridge.name, bridge.type)
						.instruction(Opcodes.ALOAD_1)
						.instruction(Opcodes.ALOAD_2)
						.instruction(Opcodes.ALOAD_3)
						.method(
							Opcodes.INVOKEINTERFACE,
							abstractAccessWriter.name,
							name,
							MethodType
								.methodType(
									Object.class,
									Method.class,
									Object.class,
									Object[].class
								)
								.toMethodDescriptorString(),
							true
						)
						.instruction(Opcodes.ARETURN)
						.max(4, 4)
					)
				);

				byte[] code = abstractAccessWriter.toByteArray();
				Class<?> declare = (Class<?>) this.define.invoke(null, code, 0, code.length, null, null);
				this.readable(finall, declare);
				code = accessWriter.toByteArray();
				this.bridge.put(bridge.name, this.allocate.invoke(this.defineAnonymous(finall, accessWriter.name, code)));
			}

			{
				Class<?> finall = Class.forName(JavaVM.VERSION <= 0x34 ? "sun.reflect.NativeConstructorAccessorImpl" : "jdk.internal.reflect.NativeConstructorAccessorImpl");
				ClassWriter abstractAccessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.INTERFACE | AccessFlag.ABSTRACT, JavaVM.random(), Generator.type(Object.class), null);
				ClassWriter accessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, JavaVM.randomAnonymous(finall), JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC], new String[]{abstractAccessWriter.name});
				FieldWriter bridge = new FieldWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL, JavaVM.random(), "L" + abstractAccessWriter.name + ";");
				this.bytecode.field(bridge);

				this.bridge(
					bridge,
					abstractAccessWriter,
					accessWriter,
					null,
					Generator.type(finall),
					"newInstance",
					"newInstance0",
					new Class<?>[]{Object.class, Constructor.class, Object[].class},
					false,
					false
				);

				byte[] code = abstractAccessWriter.toByteArray();
				Class<?> declare = (Class<?>) this.define.invoke(null, code, 0, code.length, null, null);
				this.readable(finall, declare);
				code = accessWriter.toByteArray();
				this.bridge.put(bridge.name, this.allocate.invoke(this.defineAnonymous(finall, accessWriter.name, code)));
			}

			{
				Class<?> finall = Class.forName(JavaVM.VERSION > 0x34 ? "jdk.internal.misc.Unsafe" : "sun.misc.Unsafe");
				String unsafeType = Generator.type(finall);
				ClassWriter abstractAccessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.INTERFACE | AccessFlag.ABSTRACT, JavaVM.random(), Generator.type(Object.class), null);
				ClassWriter accessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, JavaVM.randomAnonymous(finall), JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC], new String[]{abstractAccessWriter.name});
				FieldWriter bridge = new FieldWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL, JavaVM.random(), "L" + abstractAccessWriter.name + ";");
				FieldWriter access = new FieldWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL, JavaVM.random(), Generator.signature(finall));
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
				accessWriter.method(new MethodWriter()
					.set(AccessFlag.STATIC, "<clinit>", "()V")
					.attribute(new CodeWriter()
						.field(Opcodes.GETSTATIC, unsafeType, "theUnsafe", Generator.signature(finall))
						.field(Opcodes.PUTSTATIC, accessWriter.name, access.name, Generator.signature(finall))
						.instruction(Opcodes.RETURN)
						.max(1, 0)
					)
				);

				byte[] code = abstractAccessWriter.toByteArray();
				Class<?> declare = (Class<?>) this.define.invoke(null, code, 0, code.length, null, null);
				this.readable(finall, declare);
				code = accessWriter.toByteArray();
				this.bridge.put(bridge.name, this.allocate.invoke(this.defineAnonymous(finall, accessWriter.name, code)));
			}

			if (JavaVM.VERSION > Opcodes.version(16))
			{
				Class<?> finall = ClassLoader.class;
				ClassWriter abstractAccessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.INTERFACE | AccessFlag.ABSTRACT, JavaVM.random(), Generator.type(Object.class), null);
				ClassWriter accessWriter = new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, JavaVM.randomAnonymous(finall), JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC], new String[]{abstractAccessWriter.name});
				FieldWriter bridge = new FieldWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL, JavaVM.random(), "L" + abstractAccessWriter.name + ";");
				this.bytecode.field(bridge);

				abstractAccessWriter.method(new MethodWriter()
					.set(AccessFlag.PUBLIC, "defineAnonymousClass", MethodType.methodType(Class.class, Class.class, byte[].class, Object[].class).toMethodDescriptorString())
				);

				MethodKind[] pattern = {
					new MethodKind("defineClass0", Class.class, ClassLoader.class, Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class, boolean.class, int.class, Object.class),
					new MethodKind("defineClassInternal", Class.class, Class.class, String.class, byte[].class, ProtectionDomain.class, boolean.class, int.class, Object.class)
				};
				MethodKind target = MethodKind.match(pattern, ClassLoader.class);
				String constant = JavaVM.random();
				Marker m1 = new Marker();
				Marker m2 = new Marker();
				Marker m3 = new Marker();
				Marker m4 = new Marker();
				Marker m5 = new Marker();
				Marker m6 = new Marker();
				Marker m7 = new Marker();
				StackMapTableWriter smt;
				accessWriter
					.method(new MethodWriter()
						.set(AccessFlag.PUBLIC, "defineAnonymousClass", MethodType.methodType(Class.class, Class.class, byte[].class, Object[].class).toMethodDescriptorString())
						.attribute(new CodeWriter()
							.instruction(Opcodes.ALOAD_1)
							.method(Opcodes.INVOKEVIRTUAL, Generator.type(Class.class), "getClassLoader", MethodType.methodType(ClassLoader.class).toMethodDescriptorString(), false)
							.variable(Opcodes.ASTORE, 4)
							.type(Opcodes.NEW, Generator.type(DataInputStream.class))
							.instruction(Opcodes.DUP)
							.type(Opcodes.NEW, Generator.type(ByteArrayInputStream.class))
							.instruction(Opcodes.DUP)
							.instruction(Opcodes.ALOAD_2)
							.method(Opcodes.INVOKEVIRTUAL, Generator.type(byte[].class), "clone", "()Ljava/lang/Object;", false)
							.type(Opcodes.CHECKCAST, Generator.type(byte[].class))
							.method(Opcodes.INVOKESPECIAL, Generator.type(ByteArrayInputStream.class), "<init>", "([B)V", false)
							.method(Opcodes.INVOKESPECIAL, Generator.type(DataInputStream.class), "<init>", "(Ljava/io/InputStream;)V", false)
							.variable(Opcodes.ASTORE, 5)
							.variable(Opcodes.ALOAD, 5)
							.number(Opcodes.BIPUSH, 8)
							.instruction(Opcodes.I2L)
							.method(Opcodes.INVOKEVIRTUAL, Generator.type(FilterInputStream.class), "skip", "(J)J", false)
							.instruction(Opcodes.POP2)
							.variable(Opcodes.ALOAD, 5)
							.method(Opcodes.INVOKEVIRTUAL, Generator.type(DataInputStream.class), "readUnsignedShort", "()I", false)
							.variable(Opcodes.ISTORE, 6)
							.variable(Opcodes.ILOAD, 6)
							.type(Opcodes.ANEWARRAY, Generator.type(byte[].class))
							.variable(Opcodes.ASTORE, 7)
							.instruction(Opcodes.ICONST_1)
							.variable(Opcodes.ISTORE, 8)
							.mark(m1)
							.variable(Opcodes.ILOAD, 8)
							.variable(Opcodes.ILOAD, 6)
							.jump(Opcodes.IF_ICMPGE, m6)
							.variable(Opcodes.ALOAD, 5)
							.method(Opcodes.INVOKEVIRTUAL, Generator.type(DataInputStream.class), "readUnsignedByte", "()I", false)
							.variable(Opcodes.ISTORE, 9)
							.variable(Opcodes.ILOAD, 9)
							.instruction(Opcodes.ICONST_1)
							.jump(Opcodes.IF_ICMPNE, m2)
							.variable(Opcodes.ALOAD, 5)
							.method(Opcodes.INVOKEVIRTUAL, Generator.type(DataInputStream.class), "readUnsignedShort", "()I", false)
							.newarray(Opcodes.ARRAY_TYPE_BYTE)
							.variable(Opcodes.ASTORE, 10)
							.jump(Opcodes.GOTO, m3)
							.mark(m2)
							.field(Opcodes.GETSTATIC, accessWriter.name, constant, "[B")
							.variable(Opcodes.ILOAD, 9)
							.instruction(Opcodes.BALOAD)
							.newarray(Opcodes.ARRAY_TYPE_BYTE)
							.variable(Opcodes.ASTORE, 10)
							.mark(m3)
							.variable(Opcodes.ALOAD, 5)
							.variable(Opcodes.ALOAD, 10)
							.method(Opcodes.INVOKEVIRTUAL, Generator.type(DataInputStream.class), "read", "([B)I", false)
							.instruction(Opcodes.POP)
							.variable(Opcodes.ALOAD, 7)
							.variable(Opcodes.ILOAD, 8)
							.variable(Opcodes.ALOAD, 10)
							.instruction(Opcodes.AASTORE)
							.variable(Opcodes.ILOAD, 9)
							.number(Opcodes.BIPUSH, 5)
							.jump(Opcodes.IF_ICMPEQ, m4)
							.variable(Opcodes.ILOAD, 9)
							.number(Opcodes.BIPUSH, 6)
							.jump(Opcodes.IF_ICMPEQ, m4)
							.jump(Opcodes.GOTO, m5)
							.mark(m4)
							.iinc(8, 1)
							.mark(m5)
							.iinc(8, 1)
							.jump(Opcodes.GOTO, m1)
							.mark(m6)
							.variable(Opcodes.ALOAD, 5)
							.method(Opcodes.INVOKEVIRTUAL, Generator.type(DataInputStream.class), "readShort", "()S", false)
							.instruction(Opcodes.POP)
							.variable(Opcodes.ALOAD, 5)
							.method(Opcodes.INVOKEVIRTUAL, Generator.type(DataInputStream.class), "readUnsignedShort", "()I", false)
							.variable(Opcodes.ISTORE, 5)
							.type(Opcodes.NEW, "java/lang/String")
							.instruction(Opcodes.DUP)
							.variable(Opcodes.ALOAD, 7)
							.variable(Opcodes.ALOAD, 7)
							.variable(Opcodes.ILOAD, 5)
							.instruction(Opcodes.AALOAD)
							.instruction(Opcodes.ICONST_0)
							.instruction(Opcodes.BALOAD)
							.number(Opcodes.BIPUSH, 8)
							.instruction(Opcodes.ISHL)
							.variable(Opcodes.ALOAD, 7)
							.variable(Opcodes.ILOAD, 5)
							.instruction(Opcodes.AALOAD)
							.instruction(Opcodes.ICONST_1)
							.instruction(Opcodes.BALOAD)
							.instruction(Opcodes.IOR)
							.instruction(Opcodes.AALOAD)
							.field(Opcodes.GETSTATIC, Generator.type(StandardCharsets.class), "UTF_8", Generator.signature(Charset.class))
							.method(Opcodes.INVOKESPECIAL, "java/lang/String", "<init>", MethodType.methodType(void.class, byte[].class, Charset.class).toMethodDescriptorString(), false)
							.number(Opcodes.BIPUSH, '/')
							.number(Opcodes.BIPUSH, '.')
							.method(
								Opcodes.INVOKEVIRTUAL,
								Generator.type(String.class),
								"replace",
								MethodType.methodType(String.class, char.class, char.class).toMethodDescriptorString(),
								false
							)
							.variable(Opcodes.ASTORE, 5)
							.consume(codeWriter ->
							{
								if (UnsafeBuilder.this.bootstrap != null)
								{
									codeWriter
										.variable(Opcodes.ALOAD, 4)
										.jump(Opcodes.IFNONNULL, m7)
										.field(
											Opcodes.GETSTATIC,
											Generator.type(ClassLoader.class),
											"bootstrapClassLoader",
											Generator.signature(ClassLoader.class)
										)
										.variable(Opcodes.ASTORE, 4)
										.mark(m7);
								}
							})
							.variable(Opcodes.ALOAD, 4)
							.instruction(Opcodes.ALOAD_1)
							.variable(Opcodes.ALOAD, 5)
							.instruction(Opcodes.ALOAD_2)
							.consume(codeWriter ->
							{
								if (UnsafeBuilder.this.bootstrap == null)
								{
									codeWriter
										.instruction(Opcodes.ICONST_0)
										.instruction(Opcodes.ALOAD_2)
										.instruction(Opcodes.ARRAYLENGTH);
								}
							})
							.instruction(Opcodes.ACONST_NULL)
							.instruction(Opcodes.ICONST_0)
							.number(Opcodes.BIPUSH, 11)
							.instruction(Opcodes.ALOAD_3)
							.method(
								this.bootstrap == null ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL,
								Generator.type(ClassLoader.class),
								target.name(),
								target.type().toMethodDescriptorString(),
								false
							)
							.instruction(Opcodes.ARETURN)
							.max(10, 11)
							.attribute(smt = new StackMapTableWriter()
								.fullFrame(
									m1,
									new Verification[]{
										Verification.objectVariable(accessWriter.name),
										Verification.objectVariable(Generator.type(Class.class)),
										Verification.objectVariable(Generator.type(byte[].class)),
										Verification.objectVariable(Generator.type(Object[].class)),
										Verification.objectVariable(Generator.type(ClassLoader.class)),
										Verification.objectVariable(Generator.type(DataInputStream.class)),
										Verification.integerVariable(),
										Verification.objectVariable(Generator.type(byte[][].class)),
										Verification.integerVariable()
									},
									new Verification[]{}
								)
								.appendFrame(m2, Verification.integerVariable())
								.appendFrame(m3, Verification.objectVariable(Generator.type(byte[].class)))
								.sameFrame(m4)
								.sameFrame(m5)
								.chopFrame(m6, 3)
							)
							.consume(codeWriter ->
							{
								if (UnsafeBuilder.this.bootstrap != null)
								{
									smt.sameFrame(m7);
								}
							})
						)
					)
					.field(new FieldWriter()
						.set(AccessFlag.PRIVATE | AccessFlag.STATIC | AccessFlag.FINAL, constant, "[B")
					)
					.method(new MethodWriter()
						.set(AccessFlag.STATIC, "<clinit>", "()V")
						.attribute(new CodeWriter()
							.number(Opcodes.BIPUSH, 21)
							.newarray(Opcodes.ARRAY_TYPE_BYTE)
							.instruction(Opcodes.DUP)
							.instruction(Opcodes.ICONST_3)
							.instruction(Opcodes.ICONST_4)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.instruction(Opcodes.ICONST_4)
							.instruction(Opcodes.ICONST_4)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.instruction(Opcodes.ICONST_5)
							.number(Opcodes.BIPUSH, 8)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 6)
							.number(Opcodes.BIPUSH, 8)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 7)
							.instruction(Opcodes.ICONST_2)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 8)
							.instruction(Opcodes.ICONST_2)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 9)
							.instruction(Opcodes.ICONST_4)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 10)
							.instruction(Opcodes.ICONST_4)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 11)
							.instruction(Opcodes.ICONST_4)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 12)
							.instruction(Opcodes.ICONST_4)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 15)
							.instruction(Opcodes.ICONST_3)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 16)
							.instruction(Opcodes.ICONST_2)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 17)
							.instruction(Opcodes.ICONST_4)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 18)
							.instruction(Opcodes.ICONST_4)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 19)
							.instruction(Opcodes.ICONST_2)
							.instruction(Opcodes.BASTORE)
							.instruction(Opcodes.DUP)
							.number(Opcodes.BIPUSH, 20)
							.instruction(Opcodes.ICONST_2)
							.instruction(Opcodes.BASTORE)
							.field(Opcodes.PUTSTATIC, accessWriter.name, constant, "[B")
							.instruction(Opcodes.RETURN)
							.max(4, 0)
						)
					);

				this.implement(this.bytecode, bridge, "defineAnonymousClass", abstractAccessWriter.name, "defineAnonymousClass", new Class<?>[]{Class.class, Class.class, byte[].class, Object[].class}, true, true);

				byte[] code = abstractAccessWriter.toByteArray();
				Class<?> declare = (Class<?>) this.define.invoke(null, code, 0, code.length, null, null);
				this.readable(finall, declare);
				code = accessWriter.toByteArray();
				this.bridge.put(bridge.name, this.allocate.invoke(this.defineAnonymous(finall, accessWriter.name, code)));
			}
		}

		return this.bytecode.toByteArray();
	}

	public void readable(Class<?> from, Class<?> into) throws Throwable
	{
		if (JavaVM.VERSION > Opcodes.version(8))
		{
			this.read.invoke(module.invoke(from), module.invoke(into), true);
		}
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

	private Class<?> defineAnonymous(Class<?> host, String name, byte[] code) throws Throwable
	{
		if (JavaVM.VERSION >= Opcodes.version(17))
		{
			if (this.bootstrap != null)
			{
				ClassLoader classLoader = host.getClassLoader();
				classLoader = classLoader != null ? classLoader : this.bootstrap;
				return (Class<?>) this.anonymous.invoke(
					classLoader,
					host,
					name,
					code,
					null,
					true,
					11,
					null
				);
			}
			return (Class<?>) this.anonymous.invoke(
				host.getClassLoader(),
				host,
				name,
				code,
				0,
				code.length,
				null,
				true,
				11,
				null
			);
		}
		return (Class<?>) this.anonymous.invoke(host, code, null);
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
		int local = 1;
		for (int i = 1; i < argument.length; i++)
		{
			Generator.load(argument[i], cw, local);
			stack += Generator.typeSize(argument[i]);
			local += Generator.typeSize(argument[i]);
		}
		cw.method(nonstatic ? (abstracts ? Opcodes.INVOKEINTERFACE :Opcodes.INVOKEVIRTUAL) : Opcodes.INVOKESTATIC, finall, into, MethodType.methodType(argument[0], Arrays.copyOfRange(argument, 1, argument.length)).toMethodDescriptorString(), abstracts);
		Generator.returner(argument[0], cw);
		cw.max(Math.max(stack, Generator.typeSize(argument[0])), stack);

		mw.attribute(cw);
		bytecode.method(mw);
	}
}
