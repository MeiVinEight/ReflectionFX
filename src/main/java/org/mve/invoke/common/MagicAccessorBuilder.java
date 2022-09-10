package org.mve.invoke.common;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.BootstrapMethodWriter;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.attribute.StackMapTableWriter;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.attribute.code.stack.verification.Verification;
import org.mve.invoke.MagicAccessor;
import org.mve.invoke.MethodKind;
import org.mve.invoke.ModuleAccess;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.StackFrame;
import org.mve.invoke.Unsafe;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.management.ManagementFactory;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MagicAccessorBuilder
{
	public static final String CLASS_NAME = "org/mve/invoke/ReflectionMagicAccessor";
	private final Map<Class<?>, Object[]> access = new HashMap<>();
	private final ClassWriter bytecode;

	public MagicAccessorBuilder()
	{
		this.bytecode = new ClassWriter()
			.set(
				Opcodes.version(8),
				AccessFlag.PUBLIC | AccessFlag.SUPER,
				CLASS_NAME,
				JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC],
				new String[]{Generator.type(MagicAccessor.class)}
			);
	}

	public byte[] build(Unsafe unsafe) throws Throwable
	{
		/*
		 * int version()
		 */
		{
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.FINAL, "version", "()I")
				.attribute(new CodeWriter()
					.number(Opcodes.BIPUSH, JavaVM.VERSION - 44)
					.instruction(Opcodes.IRETURN)
					.max(1, 1)
				)
			);
		}

		/*
		 * void setAccessible(AccessibleObject acc, boolean flag);
		 */
		{
			this.prepare(AccessibleObject.class);
			Object[] access = this.access.get(AccessibleObject.class);

			String name = JavaVM.random();
			((ClassWriter)access[1]).method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, name, MethodType.methodType(void.class, AccessibleObject.class, boolean.class).toMethodDescriptorString())
			);
			((ClassWriter)access[2]).method(new MethodWriter()
				.set(AccessFlag.PUBLIC, name, MethodType.methodType(void.class, AccessibleObject.class, boolean.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ILOAD_2)
					.field(Opcodes.PUTFIELD, Generator.type(AccessibleObject.class), "override", Generator.signature(boolean.class))
					.instruction(Opcodes.RETURN)
					.max(2, 3)
				)
			);
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "setAccessible", MethodType.methodType(void.class, AccessibleObject.class, boolean.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) access[0], "L" + ((ClassWriter) access[1]).name + ";")
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ILOAD_2)
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) access[1]).name, name, MethodType.methodType(void.class, AccessibleObject.class, boolean.class).toMethodDescriptorString(), true)
					.instruction(Opcodes.RETURN)
					.max(3, 3)
				)
			);
		}

		// Class<?> forName(String name);
		{
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.FINAL, "forName", MethodType.methodType(Class.class, String.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ICONST_0)
					.instruction(Opcodes.ALOAD_0)
					.method(
						Opcodes.INVOKEINTERFACE,
						this.bytecode.name,
						"getCallerClass",
						MethodType.methodType(Class.class).toMethodDescriptorString(),
						true
					)
					.method(
						Opcodes.INVOKEVIRTUAL,
						Generator.type(Class.class),
						"getClassLoader",
						MethodType.methodType(ClassLoader.class).toMethodDescriptorString(),
						false
					)
					.method(
						Opcodes.INVOKESTATIC,
						Generator.type(Class.class),
						"forName",
						MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class).toMethodDescriptorString(),
						false
					)
					.instruction(Opcodes.ARETURN)
					.max(3, 2)
				)
			);
		}

		// Class<?> forName(String name, boolean initialize, ClassLoader loader);
		{
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.FINAL, "forName", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ILOAD_2)
					.instruction(Opcodes.ALOAD_3)
					.method(
						Opcodes.INVOKESTATIC,
						Generator.type(Class.class),
						"forName",
						MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class).toMethodDescriptorString(),
						false
					)
					.instruction(Opcodes.ARETURN)
					.max(3, 4)
				)
			);
		}

		// Class<?> defineClass(ClassLoader loader, byte[] code);
		{
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.FINAL, "defineClass", MethodType.methodType(Class.class, ClassLoader.class, byte[].class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
					.instruction(Opcodes.ACONST_NULL)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ICONST_0)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ARRAYLENGTH)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ACONST_NULL)
					.method(Opcodes.INVOKEINTERFACE, Generator.type(Unsafe.class), "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class).toMethodDescriptorString(), true)
					.instruction(Opcodes.ARETURN)
					.max(7, 3)
				)
			);
		}

		// StackFrame[] frame();
		{
			CodeWriter code;
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.FINAL, "frame", MethodType.methodType(StackFrame[].class).toMethodDescriptorString())
				.attribute(code = new CodeWriter()));
			if (JavaVM.VERSION == 0x34)
			{
				this.prepare(SecurityManager.class);
				Object[] access = this.access.get(SecurityManager.class);

				String frameAccess = JavaVM.random();
				String f1 = JavaVM.random();
				String f2 = JavaVM.random();
				String f3 = JavaVM.random();
				String f4 = JavaVM.random();
				String f5 = JavaVM.random();
				String f6 = JavaVM.random();
				String f7 = JavaVM.random();

				{
					ClassWriter frame = new ClassWriter()
						.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, frameAccess, Generator.type(Object.class), null)
						.field(new FieldWriter()
							.set(AccessFlag.PUBLIC, f1, Generator.signature(Class.class))
						)
						.field(new FieldWriter()
							.set(AccessFlag.PUBLIC, f2, Generator.signature(String.class))
						)
						.field(new FieldWriter()
							.set(AccessFlag.PUBLIC, f3, Generator.signature(MethodType.class))
						)
						.field(new FieldWriter()
							.set(AccessFlag.PUBLIC, f4, Generator.signature(int.class))
						)
						.field(new FieldWriter()
							.set(AccessFlag.PUBLIC, f5, Generator.signature(int.class))
						)
						.field(new FieldWriter()
							.set(AccessFlag.PUBLIC, f6, Generator.signature(String.class))
						)
						.field(new FieldWriter()
							.set(AccessFlag.PUBLIC, f7, Generator.signature(boolean.class))
						)
						.method(new MethodWriter()
							.set(
								AccessFlag.PUBLIC,
								"<init>",
								MethodType.methodType(
									void.class,
									Class.class,
									String.class,
									MethodType.class,
									int.class,
									int.class,
									String.class,
									boolean.class
								).toMethodDescriptorString()
							)
							.attribute(new CodeWriter()
								.instruction(Opcodes.ALOAD_0)
								.method(Opcodes.INVOKESPECIAL, Generator.type(Object.class), "<init>", "()V", false)
								.instruction(Opcodes.ALOAD_0)
								.instruction(Opcodes.ALOAD_1)
								.field(Opcodes.PUTFIELD, frameAccess, f1, Generator.signature(Class.class))
								.instruction(Opcodes.ALOAD_0)
								.instruction(Opcodes.ALOAD_2)
								.field(Opcodes.PUTFIELD, frameAccess, f2, Generator.signature(String.class))
								.instruction(Opcodes.ALOAD_0)
								.instruction(Opcodes.ALOAD_3)
								.field(Opcodes.PUTFIELD, frameAccess, f3, Generator.signature(MethodType.class))
								.instruction(Opcodes.ALOAD_0)
								.variable(Opcodes.ILOAD, 4)
								.field(Opcodes.PUTFIELD, frameAccess, f4, Generator.signature(int.class))
								.instruction(Opcodes.ALOAD_0)
								.variable(Opcodes.ILOAD, 5)
								.field(Opcodes.PUTFIELD, frameAccess, f5, Generator.signature(int.class))
								.instruction(Opcodes.ALOAD_0)
								.variable(Opcodes.ALOAD, 6)
								.field(Opcodes.PUTFIELD, frameAccess, f6, Generator.signature(String.class))
								.instruction(Opcodes.ALOAD_0)
								.variable(Opcodes.ILOAD, 7)
								.field(Opcodes.PUTFIELD, frameAccess, f7, Generator.signature(boolean.class))
								.instruction(Opcodes.RETURN)
								.max(3, 8)
							)
						);

					byte[] classcode = frame.toByteArray();
					unsafe.defineClass(null, classcode, 0, classcode.length, null, null);
				}

				String name = JavaVM.random();
				{
					Marker m1 = new Marker();
					Marker m2 = new Marker();
					Marker m3 = new Marker();
					Marker m4 = new Marker();
					Marker m5 = new Marker();

					((ClassWriter) access[1]).method(new MethodWriter()
						.set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, name, "()[L" + frameAccess + ";")
					);

					((ClassWriter) access[2])
						.field(new FieldWriter()
							.set(AccessFlag.PRIVATE | AccessFlag.STATIC | AccessFlag.FINAL, (String) access[3], Generator.signature(SecurityManager.class))
						)
						.method(new MethodWriter()
							.set(AccessFlag.STATIC, "<clinit>", "()V")
							.attribute(new CodeWriter()
								.type(Opcodes.NEW, Generator.type(SecurityManager.class))
								.instruction(Opcodes.DUP)
								.method(Opcodes.INVOKESPECIAL, Generator.type(SecurityManager.class), "<init>", "()V", false)
								.field(Opcodes.PUTSTATIC, ((ClassWriter) access[2]).name, (String) access[3], Generator.signature(SecurityManager.class))
								.instruction(Opcodes.RETURN)
								.max(2, 0)
							)
						).method(new MethodWriter()
							.set(AccessFlag.PUBLIC, name, "()[L" + frameAccess + ";")
							.attribute(new CodeWriter()
								.field(Opcodes.GETSTATIC, ((ClassWriter) access[2]).name, (String) access[3], Generator.signature(SecurityManager.class))
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(SecurityManager.class), "getClassContext", MethodType.methodType(Class[].class).toMethodDescriptorString(), false)
								.instruction(Opcodes.ASTORE_1)
								.instruction(Opcodes.ALOAD_1)
								.instruction(Opcodes.ICONST_2)
								.instruction(Opcodes.ALOAD_1)
								.instruction(Opcodes.ARRAYLENGTH)
								.method(Opcodes.INVOKESTATIC, Generator.type(Arrays.class), "copyOfRange", MethodType.methodType(Object[].class, Object[].class, int.class, int.class).toMethodDescriptorString(), false)
								.type(Opcodes.CHECKCAST, Generator.type(Class[].class))
								.instruction(Opcodes.ASTORE_1) // class[]
								.type(Opcodes.NEW, Generator.type(Throwable.class))
								.instruction(Opcodes.DUP)
								.method(Opcodes.INVOKESPECIAL, Generator.type(Throwable.class), "<init>", "()V", false)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(Throwable.class), "getStackTrace", MethodType.methodType(StackTraceElement[].class).toMethodDescriptorString(), false)
								.instruction(Opcodes.ASTORE_2)
								.instruction(Opcodes.ALOAD_2)
								.instruction(Opcodes.ICONST_2)
								.instruction(Opcodes.ALOAD_2)
								.instruction(Opcodes.ARRAYLENGTH)
								.method(Opcodes.INVOKESTATIC, Generator.type(Arrays.class), "copyOfRange", MethodType.methodType(Object[].class, Object[].class, int.class, int.class).toMethodDescriptorString(), false)
								.type(Opcodes.CHECKCAST, Generator.type(StackTraceElement[].class))
								.instruction(Opcodes.ASTORE_2) // element[]
								.instruction(Opcodes.ALOAD_1)
								.instruction(Opcodes.ARRAYLENGTH)
								.type(Opcodes.ANEWARRAY, frameAccess)
								.instruction(Opcodes.ASTORE_3) // frame
								.instruction(Opcodes.ICONST_0)
								.variable(Opcodes.ISTORE, 4) // i
								.instruction(Opcodes.ICONST_0)
								.variable(Opcodes.ISTORE, 5) // j
								.mark(m1)
								.variable(Opcodes.ILOAD, 5)
								.instruction(Opcodes.ALOAD_1)
								.instruction(Opcodes.ARRAYLENGTH)
								.jump(Opcodes.IF_ICMPGE, m5)
								.instruction(Opcodes.ALOAD_1)
								.variable(Opcodes.ILOAD, 5)
								.instruction(Opcodes.AALOAD)
								.variable(Opcodes.ASTORE, 6) // class
								.instruction(Opcodes.ACONST_NULL)
								.variable(Opcodes.ASTORE, 7) // method
								.instruction(Opcodes.ICONST_M1)
								.variable(Opcodes.ISTORE, 8) // line
								.instruction(Opcodes.ACONST_NULL)
								.variable(Opcodes.ASTORE, 9) // file
								.instruction(Opcodes.ICONST_0)
								.variable(Opcodes.ISTORE, 10) // native
								.variable(Opcodes.ALOAD, 6)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(Class.class), "getTypeName", "()Ljava/lang/String;", false)
								.instruction(Opcodes.ALOAD_2)
								.variable(Opcodes.ILOAD, 4)
								.instruction(Opcodes.AALOAD)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(StackTraceElement.class), "getClassName", "()Ljava/lang/String;", false)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(String.class), "equals", "(Ljava/lang/Object;)Z", false)
								.jump(Opcodes.IFEQ, m2)
								.instruction(Opcodes.ALOAD_2)
								.variable(Opcodes.ILOAD, 4)
								.instruction(Opcodes.AALOAD)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(StackTraceElement.class), "getMethodName", "()Ljava/lang/String;", false)
								.variable(Opcodes.ASTORE, 7)
								.instruction(Opcodes.ALOAD_2)
								.variable(Opcodes.ILOAD, 4)
								.instruction(Opcodes.AALOAD)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(StackTraceElement.class), "getLineNumber", "()I", false)
								.variable(Opcodes.ISTORE, 8)
								.instruction(Opcodes.ALOAD_2)
								.variable(Opcodes.ILOAD, 4)
								.instruction(Opcodes.AALOAD)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(StackTraceElement.class), "getFileName", "()Ljava/lang/String;", false)
								.variable(Opcodes.ASTORE, 9)
								.instruction(Opcodes.ALOAD_2)
								.variable(Opcodes.ILOAD, 4)
								.instruction(Opcodes.AALOAD)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(StackTraceElement.class), "isNativeMethod", "()Z", false)
								.variable(Opcodes.ISTORE, 10)
								.iinc(4, 1)
								.jump(Opcodes.GOTO, m4)
								.mark(m2)
								.instruction(Opcodes.ALOAD_1)
								.instruction(Opcodes.ARRAYLENGTH)
								.instruction(Opcodes.ALOAD_2)
								.instruction(Opcodes.ARRAYLENGTH)
								.jump(Opcodes.IF_ICMPGE, m4)
								.mark(m3)
								.instruction(Opcodes.ALOAD_1)
								.instruction(Opcodes.ARRAYLENGTH)
								.variable(Opcodes.ILOAD, 5)
								.instruction(Opcodes.ISUB)
								.instruction(Opcodes.ALOAD_2)
								.instruction(Opcodes.ARRAYLENGTH)
								.variable(Opcodes.ILOAD, 4)
								.instruction(Opcodes.ISUB)
								.jump(Opcodes.IF_ICMPGE, m4)
								.iinc(4, 1)
								.variable(Opcodes.ALOAD, 6)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(Class.class), "getTypeName", "()Ljava/lang/String;", false)
								.instruction(Opcodes.ALOAD_2)
								.variable(Opcodes.ILOAD, 4)
								.instruction(Opcodes.AALOAD)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(StackTraceElement.class), "getClassName", "()Ljava/lang/String;", false)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(String.class), "equals", "(Ljava/lang/Object;)Z", false)
								.jump(Opcodes.IFEQ, m3)
								.instruction(Opcodes.ALOAD_2)
								.variable(Opcodes.ILOAD, 4)
								.instruction(Opcodes.AALOAD)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(StackTraceElement.class), "getMethodName", "()Ljava/lang/String;", false)
								.variable(Opcodes.ASTORE, 7)
								.instruction(Opcodes.ALOAD_2)
								.variable(Opcodes.ILOAD, 4)
								.instruction(Opcodes.AALOAD)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(StackTraceElement.class), "getLineNumber", "()I", false)
								.variable(Opcodes.ISTORE, 8)
								.instruction(Opcodes.ALOAD_2)
								.variable(Opcodes.ILOAD, 4)
								.instruction(Opcodes.AALOAD)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(StackTraceElement.class), "getFileName", "()Ljava/lang/String;", false)
								.variable(Opcodes.ASTORE, 9)
								.instruction(Opcodes.ALOAD_2)
								.variable(Opcodes.ILOAD, 4)
								.instruction(Opcodes.AALOAD)
								.method(Opcodes.INVOKEVIRTUAL, Generator.type(StackTraceElement.class), "isNativeMethod", "()Z", false)
								.variable(Opcodes.ISTORE, 10)
								.iinc(4, 1)
								.mark(m4)
								.instruction(Opcodes.ALOAD_3)
								.variable(Opcodes.ILOAD, 5)
								.type(Opcodes.NEW, frameAccess)
								.instruction(Opcodes.DUP)
								.variable(Opcodes.ALOAD, 6)
								.variable(Opcodes.ALOAD, 7)
								.instruction(Opcodes.ACONST_NULL)
								.instruction(Opcodes.ICONST_M1)
								.variable(Opcodes.ILOAD, 8)
								.variable(Opcodes.ALOAD, 9)
								.variable(Opcodes.ILOAD, 10)
								.method(Opcodes.INVOKESPECIAL, frameAccess, "<init>", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;IILjava/lang/String;Z)V", false)
								.instruction(Opcodes.AASTORE)
								.iinc(5, 1)
								.jump(Opcodes.GOTO, m1)
								.mark(m5)
								.instruction(Opcodes.ALOAD_3)
								.instruction(Opcodes.ARETURN)
								.max(11, 11)
							)
						);
				}
				{
					Marker m1 = new Marker();
					Marker m2 = new Marker();
					code.field(Opcodes.GETSTATIC, this.bytecode.name, (String) access[0], "L" + ((ClassWriter) access[1]).name + ";")
						.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) access[1]).name, name, "()[L" + frameAccess + ";", true)
						.instruction(Opcodes.ASTORE_1)
						.instruction(Opcodes.ALOAD_1)
						.instruction(Opcodes.ARRAYLENGTH)
						.type(Opcodes.ANEWARRAY, Generator.type(StackFrame.class))
						.instruction(Opcodes.ASTORE_2)
						.instruction(Opcodes.ICONST_0)
						.instruction(Opcodes.ISTORE_3)
						.mark(m1)
						.instruction(Opcodes.ILOAD_3)
						.instruction(Opcodes.ALOAD_1)
						.instruction(Opcodes.ARRAYLENGTH)
						.jump(Opcodes.IF_ICMPGE, m2)
						.instruction(Opcodes.ALOAD_1)
						.instruction(Opcodes.ILOAD_3)
						.instruction(Opcodes.AALOAD)
						.variable(Opcodes.ASTORE, 4)
						.instruction(Opcodes.ALOAD_2)
						.instruction(Opcodes.ILOAD_3)
						.type(Opcodes.NEW, Generator.type(StackFrame.class))
						.instruction(Opcodes.DUP)
						.variable(Opcodes.ALOAD, 4)
						.field(Opcodes.GETFIELD, frameAccess, f1, Generator.signature(Class.class))
						.variable(Opcodes.ALOAD, 4)
						.field(Opcodes.GETFIELD, frameAccess, f2, Generator.signature(String.class))
						.variable(Opcodes.ALOAD, 4)
						.field(Opcodes.GETFIELD, frameAccess, f3, Generator.signature(MethodType.class))
						.variable(Opcodes.ALOAD, 4)
						.field(Opcodes.GETFIELD, frameAccess, f4, Generator.signature(int.class))
						.variable(Opcodes.ALOAD, 4)
						.field(Opcodes.GETFIELD, frameAccess, f5, Generator.signature(int.class))
						.variable(Opcodes.ALOAD, 4)
						.field(Opcodes.GETFIELD, frameAccess, f6, Generator.signature(String.class))
						.variable(Opcodes.ALOAD, 4)
						.field(Opcodes.GETFIELD, frameAccess, f7, Generator.signature(boolean.class))
						.method(
							Opcodes.INVOKESPECIAL,
							Generator.type(StackFrame.class),
							"<init>",
							MethodType.methodType(
								void.class,
								Class.class,
								String.class,
								MethodType.class,
								int.class,
								int.class,
								String.class,
								boolean.class
							).toMethodDescriptorString(),
							false
						)
						.instruction(Opcodes.AASTORE)
						.iinc(3, 1)
						.jump(Opcodes.GOTO, m1)
						.mark(m2)
						.instruction(Opcodes.ALOAD_2)
						.instruction(Opcodes.ARETURN)
						.max(11, 5)
						.attribute(new StackMapTableWriter()
							.appendFrame(
								m1,
								Verification.objectVariable("[L" + frameAccess + ";"),
								Verification.objectVariable(Generator.type(StackFrame[].class)),
								Verification.integerVariable()
							)
							.chopFrame(m2, 1)
						);
				}
			}
			else
			{
				Marker marker = new Marker();
				this.bytecode.attribute(new BootstrapMethodWriter()
					.mark(marker)
					.bootstrap(
						new org.mve.asm.constant.MethodHandle(
							Opcodes.REFERENCE_KIND_INVOKE_STATIC,
							"java/lang/invoke/LambdaMetafactory",
							"metafactory",
							MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, MethodType.class, MethodHandle.class, MethodType.class).toMethodDescriptorString()
						),
						new org.mve.asm.constant.MethodType("(Ljava/lang/Object;)V"),
						new org.mve.asm.constant.MethodHandle(
							Opcodes.REFERENCE_KIND_INVOKE_STATIC,
							this.bytecode.name,
							"frame$lambda$0",
							"(Ljava/util/ArrayList;Ljava/lang/StackWalker$StackFrame;)V"
						),
						new org.mve.asm.constant.MethodType("(Ljava/lang/StackWalker$StackFrame;)V")
					)
				).method(new MethodWriter()
					.set(AccessFlag.PRIVATE | AccessFlag.STATIC | AccessFlag.SYNTHETIC, "frame$lambda$0", "(Ljava/util/ArrayList;Ljava/lang/StackWalker$StackFrame;)V")
					.attribute(new CodeWriter()
						.instruction(Opcodes.ALOAD_0)
						.instruction(Opcodes.ALOAD_1)
						.type(Opcodes.CHECKCAST, "java/lang/StackFrameInfo")
						.instruction(Opcodes.ASTORE_1)
						.type(Opcodes.NEW, Generator.type(StackFrame.class))
						.instruction(Opcodes.DUP)
						.instruction(Opcodes.ALOAD_1)
						.field(Opcodes.GETFIELD, "java/lang/StackFrameInfo", "memberName", "Ljava/lang/Object;")
						.type(Opcodes.CHECKCAST, "java/lang/invoke/MemberName")
						.field(Opcodes.GETFIELD, "java/lang/invoke/MemberName", "clazz", "Ljava/lang/Class;")
						.instruction(Opcodes.ALOAD_1)
						.field(Opcodes.GETFIELD, "java/lang/StackFrameInfo", "memberName", "Ljava/lang/Object;")
						.type(Opcodes.CHECKCAST, "java/lang/invoke/MemberName")
						.method(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MemberName", "getName", "()Ljava/lang/String;", false)
						.instruction(Opcodes.ALOAD_1)
						.field(Opcodes.GETFIELD, "java/lang/StackFrameInfo", "memberName", "Ljava/lang/Object;")
						.type(Opcodes.CHECKCAST, "java/lang/invoke/MemberName")
						.method(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MemberName", "getMethodType", "()Ljava/lang/invoke/MethodType;", false)
						.instruction(Opcodes.ALOAD_1)
						.method(Opcodes.INVOKEINTERFACE, "java/lang/StackWalker$StackFrame", "getByteCodeIndex", "()I", true)
						.instruction(Opcodes.ALOAD_1)
						.method(Opcodes.INVOKEINTERFACE, "java/lang/StackWalker$StackFrame", "getLineNumber", "()I", true)
						.instruction(Opcodes.ALOAD_1)
						.method(Opcodes.INVOKEINTERFACE, "java/lang/StackWalker$StackFrame", "getFileName", "()Ljava/lang/String;", true)
						.instruction(Opcodes.ALOAD_1)
						.method(Opcodes.INVOKEINTERFACE, "java/lang/StackWalker$StackFrame", "isNativeMethod", "()Z", true)
						.method(Opcodes.INVOKESPECIAL, Generator.type(StackFrame.class), "<init>", MethodType.methodType(void.class, Class.class, String.class, MethodType.class, int.class, int.class, String.class, boolean.class).toMethodDescriptorString(), false)
						.method(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false)
						.instruction(Opcodes.POP)
						.instruction(Opcodes.RETURN)
						.max(10, 2)
					)
				);
				code.type(Opcodes.NEW, Generator.type(ArrayList.class))
					.instruction(Opcodes.DUP)
					.method(Opcodes.INVOKESPECIAL, Generator.type(ArrayList.class), "<init>", "()V", false)
					.instruction(Opcodes.ASTORE_1)
					.instruction(Opcodes.ICONST_3)
					.type(Opcodes.ANEWARRAY, "java/lang/StackWalker$Option")
					.instruction(Opcodes.DUP)
					.instruction(Opcodes.ICONST_0)
					.field(Opcodes.GETSTATIC, "java/lang/StackWalker$Option", "RETAIN_CLASS_REFERENCE", "Ljava/lang/StackWalker$Option;")
					.instruction(Opcodes.AASTORE)
					.instruction(Opcodes.DUP)
					.instruction(Opcodes.ICONST_1)
					.field(Opcodes.GETSTATIC, "java/lang/StackWalker$Option", "SHOW_HIDDEN_FRAMES", "Ljava/lang/StackWalker$Option;")
					.instruction(Opcodes.AASTORE)
					.instruction(Opcodes.DUP)
					.instruction(Opcodes.ICONST_2)
					.field(Opcodes.GETSTATIC, "java/lang/StackWalker$Option", "SHOW_REFLECT_FRAMES", "Ljava/lang/StackWalker$Option;")
					.instruction(Opcodes.AASTORE)
					.method(Opcodes.INVOKESTATIC, Generator.type(Set.class), "of", MethodType.methodType(Set.class, Object[].class).toMethodDescriptorString(), true)
					.method(Opcodes.INVOKESTATIC, "java/lang/StackWalker", "getInstance", "(Ljava/util/Set;)Ljava/lang/StackWalker;", false)
					.instruction(Opcodes.ALOAD_1)
					.dynamic(marker, "accept", "(Ljava/util/ArrayList;)Ljava/util/function/Consumer;", false)
					.method(Opcodes.INVOKEVIRTUAL, "java/lang/StackWalker", "forEach", "(Ljava/util/function/Consumer;)V", false)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ICONST_0)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(ArrayList.class), "remove", "(I)Ljava/lang/Object;", false)
					.instruction(Opcodes.POP)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ICONST_0)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(ArrayList.class), "remove", "(I)Ljava/lang/Object;", false)
					.instruction(Opcodes.POP)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I", false)
					.type(Opcodes.ANEWARRAY, Generator.type(StackFrame.class))
					.method(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;", false)
					.type(Opcodes.CHECKCAST, Generator.type(StackFrame[].class))
					.instruction(Opcodes.ARETURN)
					.max(4, 2);
			}
		}

		/*
		 * Class<?> getCallerClass();
		 */
		{
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.FINAL, "getCallerClass", MethodType.methodType(Class.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, "getClassContext", MethodType.methodType(Class[].class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ICONST_1)
					.instruction(Opcodes.AALOAD)
					.instruction(Opcodes.ARETURN)
					.max(2, 1)
				)
			);
		}

		/*
		 * Class<?>[] getClassContext();
		 */
		{
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.FINAL | AccessFlag.PUBLIC, "getClassContext", MethodType.methodType(Class[].class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, "frame", MethodType.methodType(StackFrame[].class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ASTORE_1)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ARRAYLENGTH)
					.instruction(Opcodes.ICONST_1)
					.instruction(Opcodes.ISUB)
					.type(Opcodes.ANEWARRAY, Generator.type(Class.class))
					.instruction(Opcodes.ASTORE_2)
					.instruction(Opcodes.ICONST_0)
					.instruction(Opcodes.ISTORE_3)
					.mark(m1)
					.instruction(Opcodes.ILOAD_3)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ARRAYLENGTH)
					.jump(Opcodes.IF_ICMPGE, m2)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ILOAD_3)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ILOAD_3)
					.instruction(Opcodes.ICONST_1)
					.instruction(Opcodes.IADD)
					.instruction(Opcodes.AALOAD)
					.field(Opcodes.GETFIELD, Generator.type(StackFrame.class), "clazz", "Ljava/lang/Class;")
					.instruction(Opcodes.AASTORE)
					.iinc(3, 1)
					.jump(Opcodes.GOTO, m1)
					.mark(m2)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ARETURN)
					.max(5, 4)
					.attribute(new StackMapTableWriter()
						.appendFrame(m1, Verification.objectVariable(Generator.type(StackFrame[].class)), Verification.objectVariable(Generator.type(Class[].class)), Verification.integerVariable())
						.chopFrame(m2, 1)
					)
				)
			);
		}

		/*
		 * <T> T construct(Class<?> target);
		 */
		{
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.FINAL, "construct", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ICONST_0)
					.type(Opcodes.ANEWARRAY, Generator.type(Class.class))
					.instruction(Opcodes.ICONST_0)
					.type(Opcodes.ANEWARRAY, Generator.type(Object.class))
					.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, "construct", MethodType.methodType(Object.class, Class.class, Class[].class, Object[].class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ARETURN)
					.max(4, 2)
				)
			);
		}

		/*
		 * <T> T construct(Class<?> target, Class<?>[] paramTypes, Object[] params);
		 */
		{
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.FINAL, "construct", MethodType.methodType(Object.class, Class.class, Class[].class, Object[].class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, "getConstructor", MethodType.methodType(Constructor.class, Class.class, Class[].class).toMethodDescriptorString(), false)
					.variable(Opcodes.ASTORE, 4)
					.instruction(Opcodes.ALOAD_0)
					.variable(Opcodes.ALOAD, 4)
					.instruction(Opcodes.ICONST_1)
					.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, "setAccessible", MethodType.methodType(void.class, AccessibleObject.class, boolean.class).toMethodDescriptorString(), false)
					.variable(Opcodes.ALOAD, 4)
					.instruction(Opcodes.ALOAD_3)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(Constructor.class), "newInstance", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ARETURN)
					.max(3, 5)
				)
			);
		}

		/*
		 *Field getField(Class<?> target, String name);
		 */
		{
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();

			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "getField", MethodType.methodType(Field.class, Class.class, String.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, "getFields", MethodType.methodType(Field[].class, Class.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ASTORE_3)
					.instruction(Opcodes.ALOAD_3)
					.instruction(Opcodes.ARRAYLENGTH)
					.variable(Opcodes.ISTORE, 4)
					.instruction(Opcodes.ICONST_0)
					.variable(Opcodes.ISTORE, 5)
					.mark(m1)
					.variable(Opcodes.ILOAD, 5)
					.variable(Opcodes.ILOAD, 4)
					.jump(Opcodes.IF_ICMPGE, m3)
					.instruction(Opcodes.ALOAD_3)
					.variable(Opcodes.ILOAD, 5)
					.instruction(Opcodes.AALOAD)
					.variable(Opcodes.ASTORE, 6)
					.variable(Opcodes.ALOAD, 6)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(Field.class), "getName", "()Ljava/lang/String;", false)
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(String.class), "equals", "(Ljava/lang/Object;)Z", false)
					.jump(Opcodes.IFEQ, m2)
					.variable(Opcodes.ALOAD, 6)
					.instruction(Opcodes.ARETURN)
					.mark(m2)
					.iinc(5, 1)
					.jump(Opcodes.GOTO, m1)
					.mark(m3)
					.type(Opcodes.NEW, Generator.type(NoSuchFieldException.class))
					.instruction(Opcodes.DUP)
					.type(Opcodes.NEW, Generator.type(StringBuilder.class))
					.instruction(Opcodes.DUP)
					.method(Opcodes.INVOKESPECIAL, Generator.type(StringBuilder.class), "<init>", "()V", false)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(Class.class), "getTypeName", "()Ljava/lang/String;", false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
					.constant(".")
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "toString", "()Ljava/lang/String;", false)
					.method(Opcodes.INVOKESPECIAL, Generator.type(NoSuchFieldException.class), "<init>", "(Ljava/lang/String;)V", false)
					.instruction(Opcodes.ATHROW)
					.max(4, 7)
					.attribute(new StackMapTableWriter()
						.appendFrame(
							m1,
							Verification.objectVariable(Generator.type(Field[].class)),
							Verification.integerVariable(),
							Verification.integerVariable()
						)
						.appendFrame(m2, Verification.objectVariable(Generator.type(Field.class)))
						.chopFrame(m3, 2)
					)
				)
			);
		}

		/*
		 * Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes);
		 */
		{
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();

			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.VARARGS, "getMethod", MethodType.methodType(Method.class, Class.class, String.class, Class.class, Class[].class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, "getMethods", "(Ljava/lang/Class;)[Ljava/lang/reflect/Method;", false)
					.variable(Opcodes.ASTORE, 5)
					.instruction(Opcodes.ICONST_0)
					.variable(Opcodes.ISTORE, 6)
					.variable(Opcodes.ALOAD, 5)
					.instruction(Opcodes.ARRAYLENGTH)
					.variable(Opcodes.ISTORE, 7)
					.mark(m1)
					.variable(Opcodes.ILOAD, 6)
					.variable(Opcodes.ILOAD, 7)
					.jump(Opcodes.IF_ICMPGE, m3)
					.variable(Opcodes.ALOAD, 5)
					.variable(Opcodes.ILOAD, 6)
					.instruction(Opcodes.AALOAD)
					.variable(Opcodes.ASTORE, 8)
					.variable(Opcodes.ALOAD, 8)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(Method.class), "getName", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(String.class), "equals", MethodType.methodType(boolean.class, Object.class).toMethodDescriptorString(), false)
					.jump(Opcodes.IFEQ, m2)
					.variable(Opcodes.ALOAD, 8)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(Method.class), "getReturnType", MethodType.methodType(Class.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ALOAD_3)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(Class.class), "equals", MethodType.methodType(boolean.class, Object.class).toMethodDescriptorString(), false)
					.jump(Opcodes.IFEQ, m2)
					.variable(Opcodes.ALOAD, 8)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(Method.class), "getParameterTypes", MethodType.methodType(Class[].class).toMethodDescriptorString(), false)
					.variable(Opcodes.ALOAD, 4)
					.method(Opcodes.INVOKESTATIC, Generator.type(Arrays.class), "equals", MethodType.methodType(boolean.class, Object[].class, Object[].class).toMethodDescriptorString(), false)
					.jump(Opcodes.IFEQ, m2)
					.variable(Opcodes.ALOAD, 8)
					.instruction(Opcodes.ARETURN)
					.mark(m2)
					.iinc(6, 1)
					.jump(Opcodes.GOTO, m1)
					.mark(m3)
					.type(Opcodes.NEW, Generator.type(NoSuchMethodException.class))
					.instruction(Opcodes.DUP)
					.type(Opcodes.NEW, Generator.type(StringBuilder.class))
					.instruction(Opcodes.DUP)
					.method(Opcodes.INVOKESPECIAL, Generator.type(StringBuilder.class), "<init>", "()V", false)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(Class.class), "getName", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.constant(".")
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.constant(":")
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ALOAD_3)
					.variable(Opcodes.ALOAD, 4)
					.method(Opcodes.INVOKESTATIC, Generator.type(MethodType.class), "methodType", MethodType.methodType(MethodType.class, Class.class, Class[].class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(MethodType.class), "toString", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "toString", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKESPECIAL, Generator.type(NoSuchMethodException.class), "<init>", MethodType.methodType(void.class, String.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ATHROW)
					.max(5, 9)
					.attribute(new StackMapTableWriter()
						.appendFrame(m1,
							Verification.objectVariable(Generator.type(Method[].class)),
							Verification.integerVariable(),
							Verification.integerVariable()
						)
						.appendFrame(m2, Verification.objectVariable(Generator.type(Method.class)))
						.chopFrame(m3, 2)
					)
				)
			);
		}

		/*
		 * <T> Constructor<T> getConstructor(Class<?> target, Class<?>[] parameterTypes);
		 */
		{
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();

			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.VARARGS, "getConstructor", MethodType.methodType(Constructor.class, Class.class, Class[].class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, "getConstructors", MethodType.methodType(Constructor[].class, Class.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ASTORE_3)
					.instruction(Opcodes.ALOAD_3)
					.instruction(Opcodes.ARRAYLENGTH)
					.variable(Opcodes.ISTORE, 4)
					.instruction(Opcodes.ICONST_0)
					.variable(Opcodes.ISTORE, 5)
					.mark(m1)
					.variable(Opcodes.ILOAD, 5)
					.variable(Opcodes.ILOAD, 4)
					.jump(Opcodes.IF_ICMPGE, m3)
					.instruction(Opcodes.ALOAD_3)
					.variable(Opcodes.ILOAD, 5)
					.instruction(Opcodes.AALOAD)
					.variable(Opcodes.ASTORE, 6)
					.variable(Opcodes.ALOAD, 6)
					.method(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Constructor", "getParameterTypes", "()[Ljava/lang/Class;", false)
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKESTATIC, "java/util/Arrays", "equals", "([Ljava/lang/Object;[Ljava/lang/Object;)Z", false)
					.jump(Opcodes.IFEQ, m2)
					.variable(Opcodes.ALOAD, 6)
					.instruction(Opcodes.ARETURN)
					.mark(m2)
					.iinc(5, 1)
					.jump(Opcodes.GOTO, m1)
					.mark(m3)
					.type(Opcodes.NEW, Generator.type(NoSuchMethodException.class))
					.instruction(Opcodes.DUP)
					.type(Opcodes.NEW, Generator.type(StringBuilder.class))
					.instruction(Opcodes.DUP)
					.method(Opcodes.INVOKESPECIAL, Generator.type(StringBuilder.class), "<init>", "()V", false)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(Class.class), "getTypeName", "()Ljava/lang/String;", false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.constant(".<init>:")
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.field(Opcodes.GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;")
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKESTATIC, Generator.type(MethodType.class), "methodType", MethodType.methodType(MethodType.class, Class.class, Class[].class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(MethodType.class), "toString", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(StringBuilder.class), "toString", "()Ljava/lang/String;", false)
					.method(Opcodes.INVOKESPECIAL, Generator.type(NoSuchMethodException.class), "<init>", "(Ljava/lang/String;)V", false)
					.instruction(Opcodes.ATHROW)
					.max(5, 7)
					.attribute(new StackMapTableWriter()
						.appendFrame(
							m1,
							Verification.objectVariable(Generator.type(Constructor[].class)),
							Verification.integerVariable(),
							Verification.integerVariable()
						)
						.appendFrame(m2, Verification.objectVariable(Generator.type(Constructor.class)))
						.chopFrame(m3, 2)
					)
				)
			);
		}

		/*
		 * Field[] getFields(Class<?>);
		 */
		{
			this.prepare(Class.class);
			Object[] access = this.access.get(Class.class);

			String name = JavaVM.random();
			((ClassWriter) access[1]).method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, name, MethodType.methodType(Field[].class, Class.class).toMethodDescriptorString())
			);

			CodeWriter code;
			((ClassWriter) access[2]).method(new MethodWriter()
				.set(AccessFlag.PUBLIC, name, MethodType.methodType(Field[].class, Class.class).toMethodDescriptorString())
				.attribute(code = new CodeWriter())
			);
			MethodKind[] pattern = {
				new MethodKind("getDeclaredFields0", Field[].class, boolean.class),
				new MethodKind("getDeclaredFieldsImpl", Field[].class)
			};
			this.generateGetDeclared(code, pattern);

			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "getFields", MethodType.methodType(Field[].class, Class.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) access[0], "L" + ((ClassWriter) access[1]).name + ";")
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) access[1]).name, name, MethodType.methodType(Field[].class, Class.class).toMethodDescriptorString(), true)
					.instruction(Opcodes.ARETURN)
					.max(2, 2)
				)
			);
		}

		/*
		 * Method[] getMethods(Class<?>);
		 */
		{
			this.prepare(Class.class);
			this.prepare(Constructor.class);
			this.prepare(Method.class);

			Object[] classAccess = this.access.get(Class.class);
			Object[] ctrAccess = this.access.get(Constructor.class);
			Object[] methodAccess = this.access.get(Method.class);

			String getMethods = JavaVM.random();
			String getSlot = JavaVM.random();
			String getSignature = JavaVM.random();
			String getAnnotationBytes = JavaVM.random();
			String getRawParameterAnnotations = JavaVM.random();
			String newMethod = JavaVM.random();

			{
				((ClassWriter) classAccess[1]).method(new MethodWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, getMethods, MethodType.methodType(Method[].class, Class.class).toMethodDescriptorString())
				);

				CodeWriter code;
				((ClassWriter) classAccess[2]).method(new MethodWriter()
					.set(AccessFlag.PUBLIC, getMethods, MethodType.methodType(Method[].class, Class.class).toMethodDescriptorString())
					.attribute(code = new CodeWriter())
				);
				MethodKind[] pattern = {
					new MethodKind("getDeclaredMethods0", Field[].class, boolean.class),
					new MethodKind("getDeclaredMethodsImpl", Field[].class)
				};
				this.generateGetDeclared(code, pattern);
			}

			{
				((ClassWriter) ctrAccess[1]).method(new MethodWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, getSlot, MethodType.methodType(int.class, Constructor.class).toMethodDescriptorString())
				).method(new MethodWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, getSignature, MethodType.methodType(String.class, Constructor.class).toMethodDescriptorString())
				).method(new MethodWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, getAnnotationBytes, MethodType.methodType(byte[].class, Constructor.class).toMethodDescriptorString())
				).method(new MethodWriter()
					.set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, getRawParameterAnnotations, MethodType.methodType(byte[].class, Constructor.class).toMethodDescriptorString())
				);

				((ClassWriter) ctrAccess[2]).method(new MethodWriter()
					.set(AccessFlag.PUBLIC, getSlot, MethodType.methodType(int.class, Constructor.class).toMethodDescriptorString())
					.attribute(new CodeWriter()
						.instruction(Opcodes.ALOAD_1)
						.method(Opcodes.INVOKEVIRTUAL, Generator.type(Constructor.class), "getSlot", MethodType.methodType(int.class).toMethodDescriptorString(), false)
						.instruction(Opcodes.IRETURN)
						.max(1, 2)
					)
				).method(new MethodWriter()
					.set(AccessFlag.PUBLIC, getSignature, MethodType.methodType(String.class, Constructor.class).toMethodDescriptorString())
					.attribute(new CodeWriter()
						.instruction(Opcodes.ALOAD_1)
						.method(Opcodes.INVOKEVIRTUAL, Generator.type(Constructor.class), "getSignature", MethodType.methodType(String.class).toMethodDescriptorString(), false)
						.instruction(Opcodes.ARETURN)
						.max(1, 2)
					)
				).method(new MethodWriter()
					.set(AccessFlag.PUBLIC, getAnnotationBytes, MethodType.methodType(byte[].class, Constructor.class).toMethodDescriptorString())
					.attribute(new CodeWriter()
						.instruction(Opcodes.ALOAD_1)
						.method(Opcodes.INVOKEVIRTUAL, Generator.type(Constructor.class), "getAnnotationBytes", MethodType.methodType(byte[].class).toMethodDescriptorString(), false)
						.instruction(Opcodes.ARETURN)
						.max(1, 2)
					)
				).method(new MethodWriter()
					.set(AccessFlag.PUBLIC, getRawParameterAnnotations, MethodType.methodType(byte[].class, Constructor.class).toMethodDescriptorString())
					.attribute(new CodeWriter()
						.instruction(Opcodes.ALOAD_1)
						.method(Opcodes.INVOKEVIRTUAL, Generator.type(Constructor.class), "getRawParameterAnnotations", MethodType.methodType(byte[].class).toMethodDescriptorString(), false)
						.instruction(Opcodes.ARETURN)
						.max(1, 2)
					)
				);
			}

			{
				((ClassWriter) methodAccess[1]).method(new MethodWriter()
					.set(
						AccessFlag.PUBLIC | AccessFlag.ABSTRACT,
						newMethod,
						MethodType.methodType(
							Method.class,
							Class.class,
							String.class,
							Class[].class,
							Class.class,
							Class[].class,
							int.class,
							int.class,
							String.class,
							byte[].class,
							byte[].class,
							byte[].class
						).toMethodDescriptorString()
					)
				);

				((ClassWriter) methodAccess[2]).method(new MethodWriter()
					.set(
						AccessFlag.PUBLIC,
						newMethod,
						MethodType.methodType(
							Method.class,
							Class.class,
							String.class,
							Class[].class,
							Class.class,
							Class[].class,
							int.class,
							int.class,
							String.class,
							byte[].class,
							byte[].class,
							byte[].class
						).toMethodDescriptorString()
					).attribute(new CodeWriter()
						.type(Opcodes.NEW, Generator.type(Method.class))
						.instruction(Opcodes.DUP)
						.instruction(Opcodes.ALOAD_1)
						.instruction(Opcodes.ALOAD_2)
						.instruction(Opcodes.ALOAD_3)
						.variable(Opcodes.ALOAD, 4)
						.variable(Opcodes.ALOAD, 5)
						.variable(Opcodes.ILOAD, 6)
						.variable(Opcodes.ILOAD, 7)
						.variable(Opcodes.ALOAD, 8)
						.variable(Opcodes.ALOAD, 9)
						.variable(Opcodes.ALOAD, 10)
						.variable(Opcodes.ALOAD, 11)
						.method(
							Opcodes.INVOKESPECIAL,
							Generator.type(Method.class),
							"<init>",
							MethodType.methodType(
								void.class,
								Class.class,
								String.class,
								Class[].class,
								Class.class,
								Class[].class,
								int.class,
								int.class,
								String.class,
								byte[].class,
								byte[].class,
								byte[].class
							).toMethodDescriptorString(),
							false
						)
						.instruction(Opcodes.ARETURN)
						.max(13, 12)
					)
				);
			}

			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();
			Marker m4 = new Marker();

			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "getMethods", MethodType.methodType(Method[].class, Class.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) classAccess[0], "L" + ((ClassWriter) classAccess[1]).name + ";")
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) classAccess[1]).name, getMethods, MethodType.methodType(Method[].class, Class.class).toMethodDescriptorString(), true)
					.instruction(Opcodes.ASTORE_2)
					.instruction(Opcodes.ALOAD_0)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, "getConstructors", MethodType.methodType(Constructor[].class, Class.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ASTORE_3)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ARRAYLENGTH)
					.instruction(Opcodes.ALOAD_3)
					.instruction(Opcodes.ARRAYLENGTH)
					.instruction(Opcodes.IADD)
					.type(Opcodes.ANEWARRAY, Generator.type(Method.class))
					.variable(Opcodes.ASTORE, 4)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ICONST_0)
					.variable(Opcodes.ALOAD, 4)
					.instruction(Opcodes.ICONST_0)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ARRAYLENGTH)
					.method(Opcodes.INVOKESTATIC, Generator.type(System.class), "arraycopy", MethodType.methodType(void.class, Object.class, int.class, Object.class, int.class, int.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ICONST_0)
					.variable(Opcodes.ISTORE, 5)
					.mark(m1)
					.variable(Opcodes.ILOAD, 5)
					.instruction(Opcodes.ALOAD_3)
					.instruction(Opcodes.ARRAYLENGTH)
					.jump(Opcodes.IF_ICMPGE, m4)
					.variable(Opcodes.ALOAD, 4)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ARRAYLENGTH)
					.variable(Opcodes.ILOAD, 5)
					.instruction(Opcodes.IADD)
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) methodAccess[0], "L" + ((ClassWriter) methodAccess[1]).name + ";")
					.instruction(Opcodes.ALOAD_1)
					.constant("<init>")
					.instruction(Opcodes.ALOAD_3)
					.variable(Opcodes.ILOAD, 5)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(Constructor.class), "getParameterTypes", MethodType.methodType(Class[].class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ICONST_0)
					.type(Opcodes.ANEWARRAY, Generator.type(Class.class))
					.method(Opcodes.INVOKESTATIC, Generator.type(MethodType.class), "methodType", MethodType.methodType(MethodType.class, Class.class, Class[].class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(MethodType.class), "toMethodDescriptorString", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ICONST_2)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(String.class), "substring", MethodType.methodType(String.class, int.class).toMethodDescriptorString(), false)
					.constant("Q")
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(String.class), "startsWith", MethodType.methodType(boolean.class, String.class).toMethodDescriptorString(), false)
					.jump(Opcodes.IFEQ, m2)
					.instruction(Opcodes.ALOAD_1)
					.jump(Opcodes.GOTO, m3)
					.mark(m2)
					.field(Opcodes.GETSTATIC, Generator.type(Void.class), "TYPE", Generator.signature(Class.class))
					.mark(m3)
					.instruction(Opcodes.ALOAD_3)
					.variable(Opcodes.ILOAD, 5)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(Constructor.class), "getExceptionTypes", MethodType.methodType(Class[].class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ALOAD_3)
					.variable(Opcodes.ILOAD, 5)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.type(Constructor.class), "getModifiers", MethodType.methodType(int.class).toMethodDescriptorString(), false)
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) ctrAccess[0], "L" + ((ClassWriter) ctrAccess[1]).name + ";")
					.instruction(Opcodes.ALOAD_3)
					.variable(Opcodes.ILOAD, 5)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) ctrAccess[1]).name, getSlot, MethodType.methodType(int.class, Constructor.class).toMethodDescriptorString(), true)
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) ctrAccess[0], "L" + ((ClassWriter) ctrAccess[1]).name + ";")
					.instruction(Opcodes.ALOAD_3)
					.variable(Opcodes.ILOAD, 5)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) ctrAccess[1]).name, getSignature, MethodType.methodType(String.class, Constructor.class).toMethodDescriptorString(), true)
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) ctrAccess[0], "L" + ((ClassWriter) ctrAccess[1]).name + ";")
					.instruction(Opcodes.ALOAD_3)
					.variable(Opcodes.ILOAD, 5)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) ctrAccess[1]).name, getAnnotationBytes, MethodType.methodType(byte[].class, Constructor.class).toMethodDescriptorString(), true)
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) ctrAccess[0], "L" + ((ClassWriter) ctrAccess[1]).name + ";")
					.instruction(Opcodes.ALOAD_3)
					.variable(Opcodes.ILOAD, 5)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) ctrAccess[1]).name, getRawParameterAnnotations, MethodType.methodType(byte[].class, Constructor.class).toMethodDescriptorString(), true)
					.instruction(Opcodes.ACONST_NULL)
					.method(
						Opcodes.INVOKEINTERFACE,
						((ClassWriter) methodAccess[1]).name,
						newMethod,
						MethodType.methodType(
							Method.class,
							Class.class,
							String.class,
							Class[].class,
							Class.class,
							Class[].class,
							int.class,
							int.class,
							String.class,
							byte[].class,
							byte[].class,
							byte[].class
						)
						.toMethodDescriptorString(),
						true
					)
					.instruction(Opcodes.AASTORE)
					.iinc(5, 1)
					.jump(Opcodes.GOTO, m1)
					.mark(m4)
					.variable(Opcodes.ALOAD, 4)
					.instruction(Opcodes.ARETURN)
					.max(16, 6)
					.attribute(new StackMapTableWriter()
						.fullFrame(
							m1,
							new Verification[]
							{
								Verification.objectVariable(this.bytecode.name),
								Verification.objectVariable(Generator.type(Class.class)),
								Verification.objectVariable(Generator.type(Method[].class)),
								Verification.objectVariable(Generator.type(Constructor[].class)),
								Verification.objectVariable(Generator.type(Method[].class)),
								Verification.integerVariable()
							},
							new Verification[]
							{
							}

						)
						.fullFrame(
							m2,
							new Verification[]
							{
								Verification.objectVariable(this.bytecode.name),
								Verification.objectVariable(Generator.type(Class.class)),
								Verification.objectVariable(Generator.type(Method[].class)),
								Verification.objectVariable(Generator.type(Constructor[].class)),
								Verification.objectVariable(Generator.type(Method[].class)),
								Verification.integerVariable()
							},
							new Verification[]
							{
								Verification.objectVariable(Generator.type(Method[].class)),
								Verification.integerVariable(),
								Verification.objectVariable(((ClassWriter) methodAccess[1]).name),
								Verification.objectVariable(Generator.type(Class.class)),
								Verification.objectVariable(Generator.type(String.class)),
								Verification.objectVariable(Generator.type(Class[].class))
							}
						)
						.fullFrame(
							m3,
							new Verification[]
							{
								Verification.objectVariable(this.bytecode.name),
								Verification.objectVariable(Generator.type(Class.class)),
								Verification.objectVariable(Generator.type(Method[].class)),
								Verification.objectVariable(Generator.type(Constructor[].class)),
								Verification.objectVariable(Generator.type(Method[].class)),
								Verification.integerVariable()
							},
							new Verification[]
							{
								Verification.objectVariable(Generator.type(Method[].class)),
								Verification.integerVariable(),
								Verification.objectVariable(((ClassWriter) methodAccess[1]).name),
								Verification.objectVariable(Generator.type(Class.class)),
								Verification.objectVariable(Generator.type(String.class)),
								Verification.objectVariable(Generator.type(Class[].class)),
								Verification.objectVariable(Generator.type(Class.class))
							}
						)
						.chopFrame(m4, 1)
					)
				)
			);
		}

		/*
		 * <T> Constructor<T>[] getConstructors(Class<?> target);
		 */
		{
			this.prepare(Constructor.class);

			Object[] access = this.access.get(Class.class);

			String name = JavaVM.random();

			((ClassWriter) access[1]).method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, name, MethodType.methodType(Constructor[].class, Class.class).toMethodDescriptorString())
			);

			CodeWriter code;
			((ClassWriter) access[2]).method(new MethodWriter()
				.set(AccessFlag.PUBLIC, name, MethodType.methodType(Constructor[].class, Class.class).toMethodDescriptorString())
				.attribute(code = new CodeWriter())
			);
			MethodKind[] pattern = {
				new MethodKind("getDeclaredConstructors0", Constructor[].class, boolean.class),
				new MethodKind("getDeclaredConstructorsImpl", Constructor[].class)
			};
			this.generateGetDeclared(code, pattern);

			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "getConstructors", MethodType.methodType(Constructor[].class, Class.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) access[0], "L" + ((ClassWriter) access[1]).name + ";")
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) access[1]).name, name, MethodType.methodType(Constructor[].class, Class.class).toMethodDescriptorString(), true)
					.instruction(Opcodes.ARETURN)
					.max(2, 2)
				)
			);
		}

		/*
		 * void throwException(Throwable t);
		 */
		{
			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "throwException", "(Ljava/lang/Throwable;)V")
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ATHROW)
					.max(1, 2)
				)
			);
		}

		/*
		 * void initialize(Object obj);
		 */
		{
			this.prepare(Object.class);

			Object[] access = this.access.get(Object.class);

			String name = JavaVM.random();
			((ClassWriter) access[1]).method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.ABSTRACT, name, MethodType.methodType(void.class, Object.class).toMethodDescriptorString())
			);
			((ClassWriter) access[2]).method(new MethodWriter()
				.set(AccessFlag.PUBLIC, name, MethodType.methodType(void.class, Object.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKESPECIAL, Generator.type(Object.class), "<init>", "()V", false)
					.instruction(Opcodes.RETURN)
					.max(1, 2)
				)
			);

			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "initialize", MethodType.methodType(void.class, Object.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, this.bytecode.name, (String) access[0], "L" + ((ClassWriter) access[1]).name + ";")
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEINTERFACE, ((ClassWriter) access[1]).name, name, MethodType.methodType(void.class, Object.class).toMethodDescriptorString(), true)
					.instruction(Opcodes.RETURN)
					.max(2, 2)
				)
			);
		}

		/*
		 * String getName(Member member);
		 */
		{
			MethodHandles.Lookup lookup = ReflectionFactory.TRUSTED_LOOKUP;
			MethodHandle getter = lookup.findStaticGetter(Class.forName(JavaVM.VERSION == 0x34 ? "sun.reflect.Reflection" : "jdk.internal.reflect.Reflection"), "fieldFilterMap", Map.class);
			MethodHandle setter = lookup.findStaticSetter(Class.forName(JavaVM.VERSION == 0x34 ? "sun.reflect.Reflection" : "jdk.internal.reflect.Reflection"), "fieldFilterMap", Map.class);
			Map<?, ?> filter = (Map<?, ?>) getter.invoke();
			setter.invoke((Object) null);
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();

			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "getName", MethodType.methodType(String.class, Member.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_1)
					.type(Opcodes.INSTANCEOF, Generator.type(Method.class))
					.jump(Opcodes.IFEQ, m1)
					.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
					.instruction(Opcodes.ALOAD_1)
					.constant(ReflectionFactory.UNSAFE.objectFieldOffset(Method.class.getDeclaredField("name")))
					.method(Opcodes.INVOKEINTERFACE, Generator.type(Unsafe.class), "getObject", MethodType.methodType(Object.class, Object.class, long.class).toMethodDescriptorString(), true)
					.type(Opcodes.CHECKCAST, Generator.type(String.class))
					.instruction(Opcodes.ARETURN)
					.mark(m1)
					.instruction(Opcodes.ALOAD_1)
					.type(Opcodes.INSTANCEOF, Generator.type(Field.class))
					.jump(Opcodes.IFEQ, m2)
					.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
					.instruction(Opcodes.ALOAD_1)
					.constant(ReflectionFactory.UNSAFE.objectFieldOffset(Field.class.getDeclaredField("name")))
					.method(Opcodes.INVOKEINTERFACE, Generator.type(Unsafe.class), "getObject", MethodType.methodType(Object.class, Object.class, long.class).toMethodDescriptorString(), true)
					.type(Opcodes.CHECKCAST, Generator.type(String.class))
					.instruction(Opcodes.ARETURN)
					.mark(m2)
					.instruction(Opcodes.ALOAD_1)
					.type(Opcodes.INSTANCEOF, Generator.type(Constructor.class))
					.jump(Opcodes.IFEQ, m3)
					.constant("<init>")
					.instruction(Opcodes.ARETURN)
					.mark(m3)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEINTERFACE, Generator.type(Member.class), "getName", MethodType.methodType(String.class).toMethodDescriptorString(), true)
					.instruction(Opcodes.ARETURN)
					.max(4, 2)
					.attribute(new StackMapTableWriter()
						.sameFrame(m1)
						.sameFrame(m2)
						.sameFrame(m3)
					)
				)
			);
			setter.invoke(filter);
		}

		/*
		 * int getPID();
		 */
		{
			int pid;

			{
				MethodHandles.Lookup lookup = ReflectionFactory.TRUSTED_LOOKUP;
				try
				{
					pid = (int) lookup.findVirtual(
						Class.forName("sun.management.VMManagementImpl"),
						"getProcessId",
						MethodType.methodType(int.class)
					).invoke(
						lookup.findGetter(
							Class.forName("sun.management.RuntimeImpl"),
							"jvm",
							Class.forName("sun.management.VMManagement")
						).invoke(ManagementFactory.getRuntimeMXBean())
					);
				}
				catch (Throwable ignored)
				{
					pid = (int) (long) lookup.findVirtual(
						Class.forName("com.ibm.lang.management.internal.ExtendedRuntimeMXBeanImpl"),
						"getProcessIDImpl",
						MethodType.methodType(long.class)
					).invoke(ManagementFactory.getRuntimeMXBean());
				}
			}


			this.bytecode.method(new MethodWriter()
				.set(AccessFlag.PUBLIC, "getPID", MethodType.methodType(int.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.constant(pid)
					.instruction(Opcodes.IRETURN)
					.max(1, 1)
				)
			);
		}

		return this.bytecode.toByteArray();
	}

	private void generateGetDeclared(CodeWriter code, MethodKind[] pattern)
	{
		MethodKind target = MethodKind.match(pattern, Class.class);
		code.instruction(Opcodes.ALOAD_1)
			.consume(codeWriter ->
			{
				if (target.type().parameterCount() == 1)
				{
					codeWriter.instruction(Opcodes.ICONST_0);
				}
			})
			.method(Opcodes.INVOKEVIRTUAL, Generator.type(Class.class), target.name(), target.type().toMethodDescriptorString(), false)
			.instruction(Opcodes.ARETURN)
			.max(2, 2);
	}

	public void prebuild(Unsafe unsafe)
	{
		for (Map.Entry<Class<?>, Object[]> entry : this.access.entrySet())
		{
			Object[] access = entry.getValue();
			byte[] code = ((ClassWriter) access[1]).toByteArray();
			unsafe.defineClass(null, code, 0, code.length, null, null);
		}
	}

	public void postbuild(Unsafe unsafe, Class<?> c) throws Throwable
	{
		for (Map.Entry<Class<?>, Object[]> entry : this.access.entrySet())
		{
			Object[] access = entry.getValue();
			unsafe.putObject(c, unsafe.staticFieldOffset(c.getDeclaredField((String) access[0])), unsafe.allocateInstance(unsafe.defineAnonymousClass(entry.getKey(), ((ClassWriter) access[2]).toByteArray(), null)));
		}
	}

	public void prepare(Class<?> into)
	{
		if (this.access.get(into) == null)
		{
			Object[] access = new Object[]{
				JavaVM.random(),
				new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.INTERFACE | AccessFlag.ABSTRACT, JavaVM.random(), Generator.type(Object.class), null),
				new ClassWriter()
					.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, JavaVM.randomAnonymous(into), JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC], new String[0]),
				JavaVM.random()
			};
			((ClassWriter)access[2]).interfaces[0] = ((ClassWriter)access[1]).name;

			this.access.put(into, access);
			this.bytecode.field(new FieldWriter().set(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.FINAL, (String) access[0], "L" + ((ClassWriter)access[1]).name + ";"));
		}
	}
}
