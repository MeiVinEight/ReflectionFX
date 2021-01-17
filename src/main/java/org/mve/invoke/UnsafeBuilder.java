package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.FieldWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.Type;
import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.attribute.SignatureWriter;
import org.mve.util.asm.file.AccessFlag;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UnsafeBuilder
{
	public static ClassWriter build(int majorVersion, String[] constantPool, String vm) throws Throwable
	{
		Class<?> usfClass = Class.forName(majorVersion > 0x34 ? "jdk.internal.misc.Unsafe" : "sun.misc.Unsafe");
		String unsafeType = usfClass.getTypeName().replace('.', '/');
		String unsafeSignature = "L".concat(unsafeType).concat(";");
		String className = "org/mve/invoke/UnsafeWrapper";
		ClassWriter cw = new ClassWriter();
		cw.set(0x34, 0x21, className, constantPool[0], new String[]{"org/mve/invoke/Unsafe"});
		cw.addField(new FieldWriter().set(AccessFlag.ACC_PRIVATE | AccessFlag.ACC_FINAL | AccessFlag.ACC_STATIC, "final", unsafeSignature));

		// implement methods
		{
			Consumer<ClassWriter> implement = (cw1) ->
			{
				{
					MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "getJavaVMVersion", "()I");
					cw1.addMethod(mw);
					CodeWriter code = new CodeWriter();
					mw.addAttribute(code);
					code.addNumberInstruction(Opcodes.BIPUSH, majorVersion);
					code.addInstruction(Opcodes.IRETURN);
					code.setMaxs(1, 1);
				}

				{
					cw1.addMethod(new MethodWriter()
						.set(AccessFlag.ACC_PUBLIC, "getJavaVMVendor", "()Ljava/lang/String;")
						.addAttribute(new CodeWriter()
							.addConstantInstruction(vm)
							.addInstruction(Opcodes.ARETURN)
							.setMaxs(1, 1)
						)
					);
				}

				{
					cw1.addMethod(new MethodWriter()
						.set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class, Method.class, Object.class, Object[].class).toMethodDescriptorString())
						.addAttribute(new CodeWriter()
							.addInstruction(Opcodes.ALOAD_1)
							.addInstruction(Opcodes.ALOAD_2)
							.addInstruction(Opcodes.ALOAD_3)
							.addMethodInstruction(Opcodes.INVOKESTATIC, majorVersion == 0x34 ? "sun/reflect/NativeMethodAccessorImpl" : "jdk/internal/reflect/NativeMethodAccessorImpl", "invoke0", MethodType.methodType(Object.class, Method.class, Object.class, Object[].class).toMethodDescriptorString(), false)
							.addInstruction(Opcodes.ARETURN)
							.setMaxs(3, 4)
						)
					);
				}

				{
					cw1.addMethod(new MethodWriter()
						.set(AccessFlag.ACC_PUBLIC, "construct", MethodType.methodType(Object.class, Constructor.class, Object[].class).toMethodDescriptorString())
						.addAttribute(new CodeWriter()
							.addInstruction(Opcodes.ALOAD_1)
							.addInstruction(Opcodes.ALOAD_2)
							.addMethodInstruction(Opcodes.INVOKESTATIC, majorVersion <= 0x34 ? "sun/reflect/NativeConstructorAccessorImpl" : "jdk/internal/reflect/NativeConstructorAccessorImpl", "newInstance0", MethodType.methodType(Object.class, Constructor.class, Object[].class).toMethodDescriptorString(), false)
							.addInstruction(Opcodes.ARETURN)
							.setMaxs(2, 3)
						)
					);
				}

				BiConsumer<String[], Class<?>[]> method = (name, arr) ->
				{
					String desc = MethodType.methodType(arr[0], Arrays.copyOfRange(arr, 1, arr.length)).toMethodDescriptorString();
					MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, name[0], desc);
					cw1.addMethod(mw);
					if (name.length == 3) mw.addAttribute(new SignatureWriter(name[2]));
					CodeWriter code = new CodeWriter();
					mw.addAttribute(code);
					code.addFieldInstruction(Opcodes.GETSTATIC, className, "final", unsafeSignature);
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
					code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, unsafeType, name[1], desc, false);
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
					MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC | AccessFlag.ACC_FINAL, name[0], desc);
					cw1.addMethod(mw);
					if (name.length == 2 && name[1] != null)
					{
						mw.addAttribute(new SignatureWriter(name[1]));
					}
					CodeWriter code = new CodeWriter();
					mw.addAttribute(code);
					int size = arr.length;
					for (Class<?> c : arr) if (c == long.class || c == double.class) size++;
					code.addTypeInstruction(Opcodes.NEW, "java/lang/UnsupportedOperationException");
					code.addInstruction(Opcodes.DUP);
					code.addConstantInstruction(Opcodes.LDC_W, "Method "+name[0]+desc+" is unsupported at JVM version "+majorVersion);
					code.addMethodInstruction(Opcodes.INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", MethodType.methodType(void.class, String.class).toMethodDescriptorString(), false);
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
			MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_STATIC, "<clinit>", "()V");
			cw.addMethod(mw);
			CodeWriter code = new CodeWriter();
			mw.addAttribute(code);
			code.addFieldInstruction(Opcodes.GETSTATIC, "org/mve/invoke/ReflectionFactory", "TRUSTED_LOOKUP", "Ljava/lang/invoke/MethodHandles$Lookup;");
			code.addConstantInstruction(Opcodes.LDC, new Type(usfClass));
			code.addConstantInstruction(Opcodes.LDC_W, "theUnsafe");
			code.addConstantInstruction(Opcodes.LDC, new Type(usfClass));
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStaticGetter", MethodType.methodType(MethodHandle.class, Class.class, String.class, Class.class).toMethodDescriptorString(), false);
			code.addInstruction(Opcodes.ICONST_0);
			code.addTypeInstruction(Opcodes.ANEWARRAY, "java/lang/Object");
			code.addMethodInstruction(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeWithArguments", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false);
			code.addTypeInstruction(Opcodes.CHECKCAST, usfClass.getTypeName().replace('.', '/'));
			code.addFieldInstruction(Opcodes.PUTSTATIC, className, "final", unsafeSignature);
			code.addInstruction(Opcodes.RETURN);
			code.setMaxs(4, 0);
		}

		return cw;
	}
}
