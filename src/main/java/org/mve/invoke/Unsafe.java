package org.mve.invoke;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.attribute.StackMapTableWriter;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.attribute.code.stack.verification.Verification;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.JavaVM;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
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
import java.util.function.Function;

public abstract class Unsafe
{
	public static final Unsafe unsafe;
	public static final MethodHandles.Lookup TRUSTED_LOOKUP;

	public abstract Object invoke(Method method, Object obj, Object[] args);
	public abstract Object construct(Constructor<?> c, Object[] args);
	public abstract byte getByte(long offset);
	public abstract byte getByte(Object obj, long offset);
	public abstract void putByte(long offset, byte b);
	public abstract void putByte(Object obj, long offset, byte b);
	public abstract short getShort(long offset);
	public abstract short getShort(Object obj, long offset);
	public abstract void putShort(long offset, short s);
	public abstract void putShort(Object obj, long offset, short s);
	public abstract int getInt(long offset);
	public abstract int getInt(Object obj, long offset);
	public abstract void putInt(long offset, int i);
	public abstract void putInt(Object obj, long offset, int i);
	public abstract long getLong(long offset);
	public abstract long getLong(Object obj, long offset);
	public abstract void putLong(long offset, long l);
	public abstract void putLong(Object obj, long offset, long l);
	public abstract float getFloat(long offset);
	public abstract float getFloat(Object obj, long offset);
	public abstract void putFloat(long offset, float f);
	public abstract void putFloat(Object obj, long offset, float f);
	public abstract double getDouble(long offset);
	public abstract double getDouble(Object obj, long offset);
	public abstract void putDouble(long offset, double d);
	public abstract void putDouble(Object obj, long offset, double d);
	public abstract boolean getBoolean(long offset);
	public abstract boolean getBoolean(Object obj, long offset);
	public abstract void putBoolean(long offset, boolean b);
	public abstract void putBoolean(Object obj, long offset, boolean b);
	public abstract char getChar(long offset);
	public abstract char getChar(Object obj, long offset);
	public abstract void putChar(long offset, char c);
	public abstract void putChar(Object obj, long offset, char c);
	public abstract Object getObject(Object obj, long offset);
	public abstract void putObject(Object obj, long offset, Object value);
	public abstract long getAddress(long address);
	public abstract void putAddress(long address, long value);
	public abstract long allocateMemory(long length);
	public abstract long reallocateMemory(long address, long length);
	public abstract void setMemory(Object o, long offset, long bytes, byte value);
	public abstract void setMemory(long address, long bytes, byte value);
	public abstract void copyMemory(Object src, long secOff, Object dest, long destOff, long length);
	public abstract void copyMemory(long src, long dest, long length);
	public abstract void freeMemory(long address);
	public abstract long staticFieldOffset(Field f);
	public abstract long objectFieldOffset(Field f);
	public abstract Object staticFieldBase(Field f);
	public abstract boolean shouldBeInitialized(Class<?> c);
	public abstract void ensureClassInitialized(Class<?> c);
	public abstract int arrayBaseOffset(Class<?> c);
	public abstract int arrayIndexScale(Class<?> c);
	public abstract int addressSize();
	public abstract int pageSize();
	public abstract Class<?> defineClass(String name, byte[] code, int offset, int length, ClassLoader loader, ProtectionDomain protectionDomain);
	public abstract Class<?> defineAnonymousClass(Class<?> hostClass, byte[] data, Object[] cpPatches);
	public abstract Object allocateInstance(Class<?> c);
	public abstract void throwException(Throwable t);
	public abstract boolean compareAndSwapInt(Object obj, long offset, int expected, int value);
	public abstract boolean compareAndSwapLong(Object obj, long offset, long expected, long value);
	public abstract boolean compareAndSwapObject(Object obj, long offset, Object expected, Object value);
	public abstract void unpark(Object thread);
	public abstract void park(boolean isAbsolute, long time);
	public abstract void loadFence();
	public abstract void storeFence();
	public abstract void fullFence();

	static
	{
		try
		{
			String mai =
				JavaVM.VERSION <= Opcodes.version(8) ?
					"sun/reflect/MagicAccessorImpl" :
					"jdk/internal/reflect/MagicAccessorImpl";
			Class<?> classMagic = Class.forName(mai.replace('/', '.'));

			sun.misc.Unsafe sunUnsafe;
			{
				Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
				// Field sun.misc.Unsafe#theUnsafe can invoke setAccessible without any exception or warning
				field.setAccessible(true);
				sunUnsafe = (sun.misc.Unsafe) field.get(null);
			}

			MethodHandle define;
			{
				MethodHandles.lookup();
				Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
				long offset = sunUnsafe.staticFieldOffset(field);
				TRUSTED_LOOKUP = (MethodHandles.Lookup) sunUnsafe.getObject(MethodHandles.Lookup.class, offset);
				try
				{
					// AdoptOpenJDK
					@SuppressWarnings("all")
					Field accClass = MethodHandles.Lookup.class.getDeclaredField("accessClass");
					offset = sunUnsafe.objectFieldOffset(accClass);
					sunUnsafe.putObject(TRUSTED_LOOKUP, offset, classMagic);
				}
				catch (NoSuchFieldException ignored)
				{
				}
				define = TRUSTED_LOOKUP.findVirtual(
					ClassLoader.class,
					"defineClass",
					MethodType.methodType(
						Class.class,
						String.class,
						byte[].class,
						int.class,
						int.class,
						ProtectionDomain.class
					)
				);
			}

			/*
			 * MagicAccessFactory
			 * Will be deprecated when MagicAccessorImpl removed
			 */
			Class<?> classUnsafe = JavaVM.forName(new String[]{"jdk.internal.misc.Unsafe", "sun.misc.Unsafe"});
			Object unsafeJIM;
			MethodHandle theUnsafeJIM = TRUSTED_LOOKUP.findStaticGetter(classUnsafe, "theUnsafe", classUnsafe);
			unsafeJIM = theUnsafeJIM.invoke();
			MethodHandle defineJIM = TRUSTED_LOOKUP.findVirtual(
				classUnsafe,
				"defineClass",
				MethodType.methodType(
					Class.class,
					String.class,
					byte[].class,
					int.class,
					int.class,
					ClassLoader.class,
					ProtectionDomain.class
				)
			).bindTo(unsafeJIM);
			try
			{
				Class.forName(JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC].replace('/', '.'));
			}
			catch (ClassNotFoundException ignored)
			{
				byte[] code = new ClassWriter()
					.set(
						Opcodes.version(8),
						AccessFlag.PUBLIC | AccessFlag.SUPER,
						JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC],
						mai
					)
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
				defineJIM.invoke(null, code, 0, code.length, null, null);
			}

			/*
			 * UnsafeWrapper
			 */
			{
				String className = "org/mve/invoke/UnsafeWrapper";
				Class<?> clazz;
				try
				{
					clazz = Class.forName(className.replace('/', '.'));
				}
				catch (ClassNotFoundException ignored)
				{
					ClassWriter bytecode = new ClassWriter()
						.set(
							Opcodes.version(8),
							AccessFlag.PUBLIC | AccessFlag.SUPER,
							className,
							Generator.type(Unsafe.class)
						);
					ClassLoader bootstrap = null;
					{
						try
						{
							bootstrap = (ClassLoader) TRUSTED_LOOKUP.findStaticGetter(
								ClassLoader.class,
								"bootstrapClassLoader",
								ClassLoader.class
							).invoke();
						}
						catch (NoSuchFieldException ignored1)
						{
						}
					}
					Class<?> classJLA = JavaVM.forName(new String[]{
						"jdk.internal.access.JavaLangAccess",
						"jdk.internal.misc.JavaLangAccess",
						"sun.misc.JavaLangAccess"
					});
					Object JLA;
					{
						Class<?> shared = JavaVM.forName(new String[]{
							"jdk.internal.access.SharedSecrets",
							"jdk.internal.misc.SharedSecrets",
							"sun.misc.SharedSecrets"
						});
						JLA = TRUSTED_LOOKUP.findStatic(shared, "getJavaLangAccess", MethodType.methodType(classJLA))
							.invoke();
					}
					MethodHandle anonymous = TRUSTED_LOOKUP.unreflect(JavaVM.getMethod(new MethodKind[]{
						new MethodKind(
							classJLA,
							"defineClass",
							MethodType.methodType(
								Class.class,
								ClassLoader.class,
								Class.class,
								String.class,
								byte[].class,
								ProtectionDomain.class,
								boolean.class,
								int.class,
								Object.class
							)
						),
						new MethodKind(
							unsafeJIM.getClass(),
							"defineAnonymousClass",
							MethodType.methodType(Class.class, Class.class, byte[].class, Object[].class)
						)
					}));
					Function<Object[], Class<?>> defineAnonymous = (Object[] params) ->
					{
						Class<?> host = (Class<?>) params[0];
						String name = (String) params[1];
						byte[] code = (byte[]) params[2];
						try
						{
							switch (anonymous.type().parameterCount())
							{
								case 4:
								{
									return (Class<?>) anonymous.invoke(unsafeJIM, host, code, null);
								}
								case 9:
								{
									return (Class<?>) anonymous.invoke(
										JLA,
										host.getClassLoader(),
										host,
										name,
										code,
										null,
										false,
										11,
										null
									);
								}
							}
						}
						catch (Throwable ignored1)
						{
						}
						return null;
					};
					MethodHandle allocate = TRUSTED_LOOKUP.findVirtual(
						unsafeJIM.getClass(),
						"allocateInstance",
						MethodType.methodType(Object.class, Class.class)
					).bindTo(unsafeJIM);
					Map<String, Object> bridgeMap = new HashMap<>();

					int classVersion = Opcodes.version(8);
					int abstractAccess = AccessFlag.PUBLIC | AccessFlag.ABSTRACT | AccessFlag.INTERFACE;
					int instanceAccess = AccessFlag.PUBLIC | AccessFlag.SUPER;
					int bridgeAccess = AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL;
					String instanceSuper = JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC];
					MethodKind[] pattern = {
						new MethodKind(
							ClassLoader.class,
							"defineClass0",
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
						),
						new MethodKind(
							ClassLoader.class,
							"defineClassInternal",
							Class.class,
							Class.class,
							String.class,
							byte[].class,
							ProtectionDomain.class,
							boolean.class,
							int.class,
							Object.class
						)
					};
					MethodKind target = MethodKind.getMethod(pattern);

					Consumer<Object[]> generateBridge = (args) ->
					{
						ClassWriter classWriter = (ClassWriter) args[0];
						FieldWriter objective = (FieldWriter) args[1];
						String from = (String) args[2];
						String finall = (String) args[3];
						String into = (String) args[4];
						Class<?>[] argument = (Class<?>[]) args[5];
						boolean nonstatic = (boolean) args[6];
						boolean abstracts = (boolean) args[7];

						Class<?> returnType = argument[0];
						Class<?>[] parameterTypes = Arrays.copyOfRange(argument, 1, argument.length);
						MethodType methodType = MethodType.methodType(returnType, parameterTypes);
						String methodDesc = methodType.toMethodDescriptorString();

						MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, from, methodDesc);
						CodeWriter cw = new CodeWriter();

						int stack = 0;
						if (nonstatic)
						{
							cw.field(Opcodes.GETSTATIC, classWriter.name, objective.name, objective.type);
							stack++;
						}
						int local = 1;
						for (int i = 1; i < argument.length; i++)
						{
							Generator.load(argument[i], cw, local);
							int typeSize = Generator.typeSize(argument[i]);
							stack += typeSize;
							local += typeSize;
						}
						int invoke = nonstatic ?
							(abstracts ? Opcodes.INVOKEINTERFACE :Opcodes.INVOKEVIRTUAL) :
							Opcodes.INVOKESTATIC;
						cw.method(invoke, finall, into, methodDesc, abstracts);
						Generator.returner(returnType, cw);
						cw.max(Math.max(stack, Generator.typeSize(returnType)), stack + 1);

						mw.attribute(cw);
						classWriter.method(mw);
					};
					Consumer<Object[]> generateInstance = (args) ->
					{
						FieldWriter bridge = (FieldWriter) args[0];
						ClassWriter with = (ClassWriter) args[1];
						ClassWriter instance = (ClassWriter) args[2];
						FieldWriter objective = (FieldWriter) args[3];
						String finall = (String) args[4];
						String from = (String) args[5];
						String into = (String) args[6];
						Class<?>[] argument = (Class<?>[]) args[7];
						boolean nonstatic = (boolean) args[8];
						boolean abstracts = (boolean) args[9];

						int flags = AccessFlag.PUBLIC | AccessFlag.ABSTRACT;
						Class<?> returnType = argument[0];
						Class<?>[] parameterTypes = Arrays.copyOfRange(argument, 1, argument.length);
						MethodType methodType = MethodType.methodType(returnType, parameterTypes);

						with.method(new MethodWriter().set(flags, from, methodType.toMethodDescriptorString()));
						generateBridge.accept(new Object[]{instance, objective, from, finall, into, argument, nonstatic, abstracts});
						generateBridge.accept(new Object[]{bytecode, bridge, from, with.name, from, argument, true, true});
					};

					{
						Class<?> finall = JavaVM.forName(new String[]{
							"jdk.internal.reflect.NativeMethodAccessorImpl",
							"sun.reflect.NativeMethodAccessorImpl"
						});
						ClassWriter abstractWriter = new ClassWriter()
							.set(classVersion, abstractAccess, JavaVM.random(), Generator.type(Object.class));
						ClassWriter instanceWriter = new ClassWriter()
							.set(classVersion, instanceAccess, JavaVM.randomAnonymous(finall), instanceSuper, abstractWriter.name);
						FieldWriter bridge = new FieldWriter()
							.set(bridgeAccess, JavaVM.random(), "L" + abstractWriter.name + ";");
						bytecode.field(bridge);

						generateInstance.accept(new Object[]{
							bridge,
							abstractWriter,
							instanceWriter,
							null,
							Generator.type(finall),
							"invoke",
							"invoke0",
							new Class[]{Object.class, Method.class, Object.class, Object[].class},
							false,
							false
						});

						byte[] code = abstractWriter.toByteArray();
						Class<?> declare = (Class<?>) defineJIM.invoke(null, code, 0, code.length, null, null);
						ModuleAccess.read(ModuleAccess.module(finall), ModuleAccess.module(declare));
						code = instanceWriter.toByteArray();
						bridgeMap.put(bridge.name, allocate.invoke(defineAnonymous.apply(new Object[]{finall, instanceWriter.name, code})));
					}
					{
						Class<?> finall = JavaVM.forName(new String[]{
							"sun.reflect.NativeConstructorAccessorImpl",
							"jdk.internal.reflect.NativeConstructorAccessorImpl"
						});
						ClassWriter abstractWriter = new ClassWriter()
							.set(classVersion, abstractAccess, JavaVM.random(), Generator.type(Object.class));
						ClassWriter instanceWriter = new ClassWriter()
							.set(classVersion, instanceAccess, JavaVM.randomAnonymous(finall), instanceSuper, abstractWriter.name);
						FieldWriter bridge = new FieldWriter()
							.set(bridgeAccess, JavaVM.random(), "L" + abstractWriter.name + ";");
						bytecode.field(bridge);

						generateInstance.accept(new Object[]{
							bridge,
							abstractWriter,
							instanceWriter,
							null,
							Generator.type(finall),
							"newInstance",
							"newInstance0",
							new Class<?>[]{Object.class, Constructor.class, Object[].class},
							false,
							false
						});

						byte[] code = abstractWriter.toByteArray();
						Class<?> declare = (Class<?>) defineJIM.invoke(null, code, 0, code.length, null, null);
						ModuleAccess.read(ModuleAccess.module(finall), ModuleAccess.module(declare));
						code = instanceWriter.toByteArray();
						bridgeMap.put(bridge.name, allocate.invoke(defineAnonymous.apply(new Object[]{finall, instanceWriter.name, code})));
					}
					{
						Class<?> finall = unsafeJIM.getClass();
						String unsafeType = Generator.type(finall);
						ClassWriter abstractAccessWriter = new ClassWriter()
							.set(classVersion, abstractAccess, JavaVM.random(), Generator.type(Object.class));
						ClassWriter accessWriter = new ClassWriter()
							.set(classVersion, instanceAccess, JavaVM.randomAnonymous(finall), instanceSuper, abstractAccessWriter.name);
						FieldWriter bridge = new FieldWriter()
							.set(bridgeAccess, JavaVM.random(), "L" + abstractAccessWriter.name + ";");
						FieldWriter access = new FieldWriter()
							.set(bridgeAccess, JavaVM.random(), Generator.signature(finall));
						bytecode.field(bridge);
						accessWriter.field(access);

						Consumer<Object[]> unsafe = (o) -> generateInstance.accept(new Object[]{
							bridge,
							abstractAccessWriter,
							accessWriter,
							access,
							unsafeType,
							o[0],
							o[1],
							o[2],
							true,
							false
						});

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
						if (target.name() == null)
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
						Class<?> declare = (Class<?>) defineJIM.invoke(null, code, 0, code.length, null, null);
						ModuleAccess.read(ModuleAccess.module(finall), ModuleAccess.module(declare));
						code = accessWriter.toByteArray();
						bridgeMap.put(bridge.name, allocate.invoke(defineAnonymous.apply(new Object[]{finall, accessWriter.name, code})));
					}
					if (target.name() != null)
					{
						Class<?> finall = ClassLoader.class;
						ClassWriter abstractAccessWriter = new ClassWriter()
							.set(classVersion, abstractAccess, JavaVM.random(), Generator.type(Object.class));
						ClassWriter accessWriter = new ClassWriter()
							.set(classVersion, instanceAccess, JavaVM.randomAnonymous(finall), instanceSuper, abstractAccessWriter.name);
						FieldWriter bridge = new FieldWriter()
							.set(bridgeAccess, JavaVM.random(), "L" + abstractAccessWriter.name + ";");
						bytecode.field(bridge);

						abstractAccessWriter.method(new MethodWriter().set(
							AccessFlag.PUBLIC | AccessFlag.ABSTRACT,
							"defineAnonymousClass",
							MethodType.methodType(
								Class.class,
								Class.class,
								byte[].class,
								Object[].class
							).toMethodDescriptorString()
						));

						final ClassLoader bscl = bootstrap;
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
								.set(
									AccessFlag.PUBLIC,
									"defineAnonymousClass",
									MethodType.methodType(
										Class.class,
										Class.class,
										byte[].class,
										Object[].class
									).toMethodDescriptorString()
								)
								.attribute(new CodeWriter()
									.instruction(Opcodes.ALOAD_1)
									.method(
										Opcodes.INVOKEVIRTUAL,
										Generator.type(Class.class),
										"getClassLoader",
										MethodType.methodType(ClassLoader.class).toMethodDescriptorString(),
										false
									)
									.variable(Opcodes.ASTORE, 4)
									.type(Opcodes.NEW, Generator.type(DataInputStream.class))
									.instruction(Opcodes.DUP)
									.type(Opcodes.NEW, Generator.type(ByteArrayInputStream.class))
									.instruction(Opcodes.DUP)
									.instruction(Opcodes.ALOAD_2)
									.method(
										Opcodes.INVOKEVIRTUAL,
										Generator.type(byte[].class),
										"clone",
										"()Ljava/lang/Object;",
										false
									)
									.type(Opcodes.CHECKCAST, Generator.type(byte[].class))
									.method(
										Opcodes.INVOKESPECIAL,
										Generator.type(ByteArrayInputStream.class),
										"<init>",
										"([B)V",
										false
									)
									.method(
										Opcodes.INVOKESPECIAL,
										Generator.type(DataInputStream.class),
										"<init>",
										"(Ljava/io/InputStream;)V",
										false
									)
									.variable(Opcodes.ASTORE, 5)
									.variable(Opcodes.ALOAD, 5)
									.number(Opcodes.BIPUSH, 8)
									.instruction(Opcodes.I2L)
									.method(
										Opcodes.INVOKEVIRTUAL,
										Generator.type(FilterInputStream.class),
										"skip",
										"(J)J",
										false
									)
									.instruction(Opcodes.POP2)
									.variable(Opcodes.ALOAD, 5)
									.method(
										Opcodes.INVOKEVIRTUAL,
										Generator.type(DataInputStream.class),
										"readUnsignedShort",
										"()I",
										false
									)
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
									.method(
										Opcodes.INVOKEVIRTUAL,
										Generator.type(DataInputStream.class),
										"readUnsignedByte",
										"()I",
										false
									)
									.variable(Opcodes.ISTORE, 9)
									.variable(Opcodes.ILOAD, 9)
									.instruction(Opcodes.ICONST_1)
									.jump(Opcodes.IF_ICMPNE, m2)
									.variable(Opcodes.ALOAD, 5)
									.method(
										Opcodes.INVOKEVIRTUAL,
										Generator.type(DataInputStream.class),
										"readUnsignedShort",
										"()I",
										false
									)
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
									.method(
										Opcodes.INVOKEVIRTUAL,
										Generator.type(DataInputStream.class),
										"read",
										"([B)I",
										false
									)
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
									.method(
										Opcodes.INVOKEVIRTUAL,
										Generator.type(DataInputStream.class),
										"readShort",
										"()S",
										false
									)
									.instruction(Opcodes.POP)
									.variable(Opcodes.ALOAD, 5)
									.method(
										Opcodes.INVOKEVIRTUAL,
										Generator.type(DataInputStream.class),
										"readUnsignedShort",
										"()I",
										false
									)
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
									.field(
										Opcodes.GETSTATIC,
										Generator.type(StandardCharsets.class),
										"UTF_8",
										Generator.signature(Charset.class)
									)
									.method(
										Opcodes.INVOKESPECIAL,
										"java/lang/String",
										"<init>",
										"([BLjava/nio/charset/Charset;)V",
										false)
									.number(Opcodes.BIPUSH, '/')
									.number(Opcodes.BIPUSH, '.')
									.method(
										Opcodes.INVOKEVIRTUAL,
										Generator.type(String.class),
										"replace",
										"(CC)Ljava/lang/String;",
										false
									)
									.variable(Opcodes.ASTORE, 5)
									.consume(codeWriter ->
									{
										if (bscl != null)
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
										if (bscl == null)
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
										bscl == null ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL,
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
										if (bscl != null)
										{
											smt.sameFrame(m7);
										}
									})
								)
							)
							.field(new FieldWriter()
								.set(bridgeAccess, constant, "[B")
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

						generateBridge.accept(new Object[]{
							bytecode,
							bridge,
							"defineAnonymousClass",
							abstractAccessWriter.name,
							"defineAnonymousClass",
							new Class<?>[]{Class.class, Class.class, byte[].class, Object[].class},
							true,
							true
						});

						byte[] code = abstractAccessWriter.toByteArray();
						Class<?> declare = (Class<?>) defineJIM.invoke(null, code, 0, code.length, null, null);
						ModuleAccess.read(ModuleAccess.module(finall), ModuleAccess.module(declare));
						code = accessWriter.toByteArray();
						bridgeMap.put(bridge.name, allocate.invoke(defineAnonymous.apply(new Object[]{finall, accessWriter.name, code})));
					}

					byte[] code = bytecode.toByteArray();
					clazz = (Class<?>) define.invoke(Unsafe.class.getClassLoader(), null, code, 0, code.length, null);
					Class<?> unsafeClass = unsafeJIM.getClass();

					MethodType methodType = MethodType.methodType(void.class, Object.class, long.class, Object.class);
					MethodHandle staticFieldOffset = TRUSTED_LOOKUP.findVirtual(
						unsafeClass,
						"staticFieldOffset",
						MethodType.methodType(long.class, Field.class)
					).bindTo(unsafeJIM);
					MethodHandle putObject = TRUSTED_LOOKUP.unreflect(JavaVM.getMethod(new MethodKind[]{
						new MethodKind(unsafeClass, "putReference", methodType),
						new MethodKind(unsafeClass, "putObject", methodType)
					})).bindTo(unsafeJIM);

					for (Map.Entry<String, Object> entry : bridgeMap.entrySet())
					{
						putObject.invoke(clazz, staticFieldOffset.invoke(clazz.getDeclaredField(entry.getKey())), entry.getValue());
					}
				}

				unsafe = (Unsafe) sunUnsafe.allocateInstance(clazz);
			}

		}
		catch (Throwable t)
		{
			JavaVM.thrown(t);
			throw new UnknownError();
		}
	}
}
