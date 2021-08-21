package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.attribute.BootstrapMethodWriter;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.attribute.SignatureWriter;
import org.mve.asm.attribute.SourceWriter;
import org.mve.asm.attribute.StackMapTableWriter;
import org.mve.asm.AccessFlag;
import org.mve.invoke.MagicAccessor;
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
import java.util.Map;
import java.util.Set;

public class MagicAccessorBuilder
{
	public static ClassWriter build(String[] constantPool, int vmVersion, boolean openJ9VM) throws Throwable
	{
		String className = "org/mve/invoke/ReflectionMagicAccessor";
		ClassWriter cw = new ClassWriter();
		cw.set(vmVersion, AccessFlag.PUBLIC | AccessFlag.SUPER | AccessFlag.FINAL, className, constantPool[0], new String[]{Generator.getType(MagicAccessor.class)});
		cw.attribute(new SourceWriter("MagicAccessor.java"));

		if (vmVersion == 0x34)
		{
			// 0 SecurityManager
			cw.field(new FieldWriter().set(AccessFlag.PRIVATE | AccessFlag.STATIC | AccessFlag.FINAL, "0", Generator.getSignature(SecurityManager.class)));
		}

		/*
		 * <clinit>
		 */
		if (vmVersion == 0x34)
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.STATIC, "<clinit>", "()V");
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);
			code.type(Opcodes.NEW, Generator.getType(SecurityManager.class))
				.instruction(Opcodes.DUP)
				.method(Opcodes.INVOKESPECIAL, Generator.getType(SecurityManager.class), "<init>", "()V", false)
				.field(Opcodes.PUTSTATIC, className, "0", Generator.getSignature(SecurityManager.class))
				.instruction(Opcodes.RETURN)
				.max(2, 0);
		}

		/*
		 * int version()
		 */
		{
			cw.method(new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.FINAL, "version", "()I")
				.attribute(new CodeWriter()
					.number(Opcodes.BIPUSH, vmVersion - 44)
					.instruction(Opcodes.IRETURN)
					.max(1, 1)
				)
			);
		}

		/*
		 * void setAccessible(AccessibleObject acc, boolean flag);
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC | AccessFlag.FINAL, "setAccessible", MethodType.methodType(void.class, AccessibleObject.class, boolean.class).toMethodDescriptorString());
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);
			code.instruction(Opcodes.ALOAD_1);
			code.instruction(Opcodes.ILOAD_2);
			code.field(Opcodes.PUTFIELD, Generator.getType(AccessibleObject.class), "override", Generator.getSignature(boolean.class));
			code.instruction(Opcodes.RETURN);
			code.max(2, 3);
		}

		// Class<?> forName(String name);
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC | AccessFlag.FINAL, "forName", MethodType.methodType(Class.class, String.class).toMethodDescriptorString());
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);
			if (openJ9VM)
			{
				code.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ICONST_0)
					.instruction(Opcodes.ALOAD_0)
					.method(Opcodes.INVOKESPECIAL, className, "getCallerClass", MethodType.methodType(Class.class).toMethodDescriptorString(), false)
					.field(Opcodes.GETFIELD, Generator.getType(Class.class), "classLoader", Generator.getSignature(ClassLoader.class))
					.method(Opcodes.INVOKESTATIC, Generator.getType(Class.class), "forNameImpl", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ARETURN)
					.max(3, 2);
			}
			else
			{
				code.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ICONST_0)
					.instruction(Opcodes.ALOAD_0)
					.method(Opcodes.INVOKESPECIAL, className, "getCallerClass", MethodType.methodType(Class.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.DUP)
					.instruction(Opcodes.ASTORE_2)
					.field(Opcodes.GETFIELD, Generator.getType(Class.class), "classLoader", Generator.getSignature(ClassLoader.class))
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKESTATIC, Generator.getType(Class.class), "forName0", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class, Class.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ARETURN)
					.max(4, 3);
			}
		}

		// Class<?> forName(String name, boolean initialize, ClassLoader loader);
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC | AccessFlag.FINAL, "forName", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class).toMethodDescriptorString());
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);
			if (openJ9VM)
			{
				code.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ILOAD_2)
					.instruction(Opcodes.ALOAD_3)
					.method(Opcodes.INVOKESTATIC, Generator.getType(Class.class), "forNameImpl", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ARETURN)
					.max(3, 4);
			}
			else
			{
				code.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ILOAD_2)
					.instruction(Opcodes.ALOAD_3)
					.instruction(Opcodes.ALOAD_0)
					.method(Opcodes.INVOKESPECIAL, className, "getCallerClass", MethodType.methodType(Class.class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKESTATIC, Generator.getType(Class.class), "forName0", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class, Class.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ARETURN)
					.max(4, 4);
			}
		}

		// Class<?> defineClass(ClassLoader loader, byte[] code);
		{
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.FINAL, "defineClass", MethodType.methodType(Class.class, ClassLoader.class, byte[].class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
					.instruction(Opcodes.ACONST_NULL)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ICONST_0)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ARRAYLENGTH)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ACONST_NULL)
					.method(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class).toMethodDescriptorString(), true)
					.instruction(Opcodes.ARETURN)
					.max(7, 3)
				);
			cw.method(mw);
		}

		// StackFrame[] frame();
		{
			CodeWriter code = new CodeWriter();
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.FINAL, "frame", MethodType.methodType(StackFrame[].class).toMethodDescriptorString())
				.attribute(code);
			cw.method(mw);
			if (vmVersion == 0x34)
			{
				Marker m1 = new Marker();
				Marker m2 = new Marker();
				Marker m3 = new Marker();
				Marker m4 = new Marker();
				Marker m5 = new Marker();
				code.field(Opcodes.GETSTATIC, className, "0", Generator.getSignature(SecurityManager.class))
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(SecurityManager.class), "getClassContext", MethodType.methodType(Class[].class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ASTORE_1)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ICONST_1)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ARRAYLENGTH)
					.method(Opcodes.INVOKESTATIC, Generator.getType(Arrays.class), "copyOfRange", MethodType.methodType(Object[].class, Object[].class, int.class, int.class).toMethodDescriptorString(), false)
					.type(Opcodes.CHECKCAST, Generator.getType(Class[].class))
					.instruction(Opcodes.ASTORE_1) // class[]
					.type(Opcodes.NEW, Generator.getType(Throwable.class))
					.instruction(Opcodes.DUP)
					.method(Opcodes.INVOKESPECIAL, Generator.getType(Throwable.class), "<init>", "()V", false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Throwable.class), "getStackTrace", MethodType.methodType(StackTraceElement[].class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ASTORE_2)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ICONST_1)
					.instruction(Opcodes.ALOAD_2)
					.instruction(Opcodes.ARRAYLENGTH)
					.method(Opcodes.INVOKESTATIC, Generator.getType(Arrays.class), "copyOfRange", MethodType.methodType(Object[].class, Object[].class, int.class, int.class).toMethodDescriptorString(), false)
					.type(Opcodes.CHECKCAST, Generator.getType(StackTraceElement[].class))
					.instruction(Opcodes.ASTORE_2) // element[]
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ARRAYLENGTH)
					.type(Opcodes.ANEWARRAY, Generator.getType(StackFrame.class))
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
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Class.class), "getTypeName", "()Ljava/lang/String;", false)
					.instruction(Opcodes.ALOAD_2)
					.variable(Opcodes.ILOAD, 4)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StackTraceElement.class), "getClassName", "()Ljava/lang/String;", false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(String.class), "equals", "(Ljava/lang/Object;)Z", false)
					.jump(Opcodes.IFEQ, m2)
					.instruction(Opcodes.ALOAD_2)
					.variable(Opcodes.ILOAD, 4)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StackTraceElement.class), "getMethodName", "()Ljava/lang/String;", false)
					.variable(Opcodes.ASTORE, 7)
					.instruction(Opcodes.ALOAD_2)
					.variable(Opcodes.ILOAD, 4)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StackTraceElement.class), "getLineNumber", "()I", false)
					.variable(Opcodes.ISTORE, 8)
					.instruction(Opcodes.ALOAD_2)
					.variable(Opcodes.ILOAD, 4)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StackTraceElement.class), "getFileName", "()Ljava/lang/String;", false)
					.variable(Opcodes.ASTORE, 9)
					.instruction(Opcodes.ALOAD_2)
					.variable(Opcodes.ILOAD, 4)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StackTraceElement.class), "isNativeMethod", "()Z", false)
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
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Class.class), "getTypeName", "()Ljava/lang/String;", false)
					.instruction(Opcodes.ALOAD_2)
					.variable(Opcodes.ILOAD, 4)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StackTraceElement.class), "getClassName", "()Ljava/lang/String;", false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(String.class), "equals", "(Ljava/lang/Object;)Z", false)
					.jump(Opcodes.IFEQ, m3)
					.instruction(Opcodes.ALOAD_2)
					.variable(Opcodes.ILOAD, 4)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StackTraceElement.class), "getMethodName", "()Ljava/lang/String;", false)
					.variable(Opcodes.ASTORE, 7)
					.instruction(Opcodes.ALOAD_2)
					.variable(Opcodes.ILOAD, 4)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StackTraceElement.class), "getLineNumber", "()I", false)
					.variable(Opcodes.ISTORE, 8)
					.instruction(Opcodes.ALOAD_2)
					.variable(Opcodes.ILOAD, 4)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StackTraceElement.class), "getFileName", "()Ljava/lang/String;", false)
					.variable(Opcodes.ASTORE, 9)
					.instruction(Opcodes.ALOAD_2)
					.variable(Opcodes.ILOAD, 4)
					.instruction(Opcodes.AALOAD)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StackTraceElement.class), "isNativeMethod", "()Z", false)
					.variable(Opcodes.ISTORE, 10)
					.iinc(4, 1)
					.mark(m4)
					.instruction(Opcodes.ALOAD_3)
					.variable(Opcodes.ILOAD, 5)
					.type(Opcodes.NEW, Generator.getType(StackFrame.class))
					.instruction(Opcodes.DUP)
					.variable(Opcodes.ALOAD, 6)
					.variable(Opcodes.ALOAD, 7)
					.instruction(Opcodes.ACONST_NULL)
					.instruction(Opcodes.ICONST_M1)
					.variable(Opcodes.ILOAD, 8)
					.variable(Opcodes.ALOAD, 9)
					.variable(Opcodes.ILOAD, 10)
					.method(Opcodes.INVOKESPECIAL, Generator.getType(StackFrame.class), "<init>", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;IILjava/lang/String;Z)V", false)
					.instruction(Opcodes.AASTORE)
					.iinc(5, 1)
					.jump(Opcodes.GOTO, m1)
					.mark(m5)
					.instruction(Opcodes.ALOAD_3)
					.instruction(Opcodes.ARETURN)
					.max(11, 11);

			}
			else
			{
				Marker marker = new Marker();
				cw.attribute(new BootstrapMethodWriter()
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
							className,
							"frame$lambda$0",
							"(Ljava/util/ArrayList;Ljava/lang/StackWalker$StackFrame;)V"
						),
						new org.mve.asm.constant.MethodType("(Ljava/lang/StackWalker$StackFrame;)V")
					)
				);
				cw.method(new MethodWriter()
					.set(AccessFlag.PRIVATE | AccessFlag.STATIC | AccessFlag.SYNTHETIC, "frame$lambda$0", "(Ljava/util/ArrayList;Ljava/lang/StackWalker$StackFrame;)V")
					.attribute(new CodeWriter()
						.instruction(Opcodes.ALOAD_0)
						.instruction(Opcodes.ALOAD_1)
						.type(Opcodes.CHECKCAST, "java/lang/StackFrameInfo")
						.instruction(Opcodes.ASTORE_1)
						.type(Opcodes.NEW, Generator.getType(StackFrame.class))
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
						.method(Opcodes.INVOKESPECIAL, Generator.getType(StackFrame.class), "<init>", MethodType.methodType(void.class, Class.class, String.class, MethodType.class, int.class, int.class, String.class, boolean.class).toMethodDescriptorString(), false)
						.method(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false)
						.instruction(Opcodes.POP)
						.instruction(Opcodes.RETURN)
						.max(10, 2)
					)
				);
				code.type(Opcodes.NEW, Generator.getType(ArrayList.class))
					.instruction(Opcodes.DUP)
					.method(Opcodes.INVOKESPECIAL, Generator.getType(ArrayList.class), "<init>", "()V", false)
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
					.method(Opcodes.INVOKESTATIC, Generator.getType(Set.class), "of", MethodType.methodType(Set.class, Object[].class).toMethodDescriptorString(), true)
					.method(Opcodes.INVOKESTATIC, "java/lang/StackWalker", "getInstance", "(Ljava/util/Set;)Ljava/lang/StackWalker;", false)
					.instruction(Opcodes.ALOAD_1)
					.dynamic(marker, "accept", "(Ljava/util/ArrayList;)Ljava/util/function/Consumer;", false)
					.method(Opcodes.INVOKEVIRTUAL, "java/lang/StackWalker", "forEach", "(Ljava/util/function/Consumer;)V", false)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ICONST_0)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(ArrayList.class), "remove", "(I)Ljava/lang/Object;", false)
					.instruction(Opcodes.POP)
					.instruction(Opcodes.ALOAD_1)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I", false)
					.type(Opcodes.ANEWARRAY, Generator.getType(StackFrame.class))
					.method(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;", false)
					.type(Opcodes.CHECKCAST, Generator.getType(StackFrame[].class))
					.instruction(Opcodes.ARETURN)
					.max(4, 2);
			}
		}

		/*
		 * Class<?> getCallerClass();
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC | AccessFlag.FINAL, "getCallerClass", MethodType.methodType(Class.class).toMethodDescriptorString());
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);
			code.instruction(Opcodes.ALOAD_0)
				.method(Opcodes.INVOKEVIRTUAL, className, "getClassContext", MethodType.methodType(Class[].class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ICONST_1)
				.instruction(Opcodes.AALOAD)
				.instruction(Opcodes.ARETURN)
				.max(2, 1);
		}

		/*
		 * Class<?>[] getClassContext();
		 */
		{
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.FINAL | AccessFlag.PUBLIC, "getClassContext", MethodType.methodType(Class[].class).toMethodDescriptorString())
				.attribute(new SignatureWriter("()[Ljava/lang/Class<*>;"));
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			code.instruction(Opcodes.ALOAD_0)
				.method(Opcodes.INVOKEVIRTUAL, className, "frame", MethodType.methodType(StackFrame[].class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ASTORE_1)
				.instruction(Opcodes.ALOAD_1)
				.instruction(Opcodes.ARRAYLENGTH)
				.type(Opcodes.ANEWARRAY, Generator.getType(Class.class))
				.instruction(Opcodes.ASTORE_2)
				.instruction(Opcodes.ICONST_0)
				.instruction(Opcodes.ISTORE_3)
				.mark(m1)
				.instruction(Opcodes.ALOAD_3)
				.instruction(Opcodes.ALOAD_2)
				.jump(Opcodes.IF_ICMPGE, m2)
				.instruction(Opcodes.ALOAD_2)
				.instruction(Opcodes.ILOAD_3)
				.instruction(Opcodes.ALOAD_1)
				.instruction(Opcodes.ILOAD_3)
				.instruction(Opcodes.AALOAD)
				.field(Opcodes.GETFIELD, Generator.getType(StackFrame.class), "clazz", "Ljava/lang/Class;")
				.instruction(Opcodes.AASTORE)
				.iinc(3, 1)
				.jump(Opcodes.GOTO, m1)
				.mark(m2)
				.instruction(Opcodes.ALOAD_2)
				.instruction(Opcodes.ARETURN)
				.max(4, 4);
		}

		/*
		 * <T> T construct(Class<?> target);
		 */
		{
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.FINAL, "construct", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString())
				.attribute(new SignatureWriter("<T:Ljava/lang/Object;>(Ljava/lang/Class<*>;)TT;"));
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);
			code.instruction(Opcodes.ALOAD_1);
			code.instruction(Opcodes.ICONST_0);
			code.type(Opcodes.ANEWARRAY, Generator.getType(Class.class));
			code.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Class.class), "getDeclaredConstructor", MethodType.methodType(Constructor.class, Class[].class).toMethodDescriptorString(), false);
			code.instruction(Opcodes.DUP);
			code.instruction(Opcodes.ICONST_1);
			code.field(Opcodes.PUTFIELD, Generator.getType(AccessibleObject.class), "override", Generator.getSignature(boolean.class));
			code.instruction(Opcodes.ICONST_0);
			code.type(Opcodes.ANEWARRAY, Generator.getType(Object.class));
			code.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Constructor.class), "newInstance", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false);
			code.instruction(Opcodes.ARETURN);
			code.max(3, 2);
		}

		/*
		 * <T> T construct(Class<?> target, Class<?>[] paramTypes, Object[] params);
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC | AccessFlag.FINAL, "construct", MethodType.methodType(Object.class, Class.class, Class[].class, Object[].class).toMethodDescriptorString());
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);
			code.instruction(Opcodes.ALOAD_1);
			code.instruction(Opcodes.ALOAD_2);
			code.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Class.class), "getDeclaredConstructor", MethodType.methodType(Constructor.class, Class[].class).toMethodDescriptorString(), false);
			code.instruction(Opcodes.DUP);
			code.instruction(Opcodes.ICONST_1);
			code.field(Opcodes.PUTFIELD, Generator.getType(AccessibleObject.class), "override", Generator.getSignature(boolean.class));
			code.instruction(Opcodes.ALOAD_3);
			code.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Constructor.class), "newInstance", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false);
			code.instruction(Opcodes.ARETURN);
			code.max(3, 4);
		}

		/*
		 *Field getField(Class<?> target, String name);
		 */
		{
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.PUBLIC, "getField", MethodType.methodType(Field.class, Class.class, String.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKESPECIAL, className, "getFields", MethodType.methodType(Field[].class, Class.class).toMethodDescriptorString(), false)
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
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Field.class), "getName", "()Ljava/lang/String;", false)
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(String.class), "equals", "(Ljava/lang/Object;)Z", false)
					.jump(Opcodes.IFEQ, m2)
					.variable(Opcodes.ALOAD, 6)
					.instruction(Opcodes.ARETURN)
					.mark(m2)
					.iinc(5, 1)
					.jump(Opcodes.GOTO, m1)
					.mark(m3)
					.type(Opcodes.NEW, Generator.getType(NoSuchFieldException.class))
					.instruction(Opcodes.DUP)
					.type(Opcodes.NEW, Generator.getType(StringBuilder.class))
					.instruction(Opcodes.DUP)
					.method(Opcodes.INVOKESPECIAL, Generator.getType(StringBuilder.class), "<init>", "()V", false)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Class.class), "getTypeName", "()Ljava/lang/String;", false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
					.constant(".")
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "toString", "()Ljava/lang/String;", false)
					.method(Opcodes.INVOKESPECIAL, Generator.getType(NoSuchFieldException.class), "<init>", "(Ljava/lang/String;)V", false)
					.instruction(Opcodes.ATHROW)
					.max(4, 7)
				);
			cw.method(mw);

		}

		/*
		 * Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes);
		 */
		{
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.PUBLIC | AccessFlag.VARARGS, "getMethod", MethodType.methodType(Method.class, Class.class, String.class, Class.class, Class[].class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_0)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKESPECIAL, className, "getMethods", "(Ljava/lang/Class;)[Ljava/lang/reflect/Method;", false)
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
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Method.class), "getName", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(String.class), "equals", MethodType.methodType(boolean.class, Object.class).toMethodDescriptorString(), false)
					.jump(Opcodes.IFEQ, m2)
					.variable(Opcodes.ALOAD, 8)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Method.class), "getReturnType", MethodType.methodType(Class.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ALOAD_3)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Class.class), "equals", MethodType.methodType(boolean.class, Object.class).toMethodDescriptorString(), false)
					.jump(Opcodes.IFEQ, m2)
					.variable(Opcodes.ALOAD, 8)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Method.class), "getParameterTypes", MethodType.methodType(Class[].class).toMethodDescriptorString(), false)
					.variable(Opcodes.ALOAD, 4)
					.method(Opcodes.INVOKESTATIC, Generator.getType(Arrays.class), "equals", MethodType.methodType(boolean.class, Object[].class, Object[].class).toMethodDescriptorString(), false)
					.jump(Opcodes.IFEQ, m2)
					.variable(Opcodes.ALOAD, 8)
					.instruction(Opcodes.ARETURN)
					.mark(m2)
					.iinc(6, 1)
					.jump(Opcodes.GOTO, m1)
					.mark(m3)
					.type(Opcodes.NEW, Generator.getType(NoSuchMethodException.class))
					.instruction(Opcodes.DUP)
					.type(Opcodes.NEW, Generator.getType(StringBuilder.class))
					.instruction(Opcodes.DUP)
					.method(Opcodes.INVOKESPECIAL, Generator.getType(StringBuilder.class), "<init>", "()V", false)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Class.class), "getName", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.constant(".")
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ALOAD_2)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.constant(":")
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ALOAD_3)
					.variable(Opcodes.ALOAD, 4)
					.method(Opcodes.INVOKESTATIC, Generator.getType(MethodType.class), "methodType", MethodType.methodType(MethodType.class, Class.class, Class[].class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(MethodType.class), "toString", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "toString", MethodType.methodType(String.class).toMethodDescriptorString(), false)
					.method(Opcodes.INVOKESPECIAL, Generator.getType(NoSuchMethodException.class), "<init>", MethodType.methodType(void.class, String.class).toMethodDescriptorString(), false)
					.instruction(Opcodes.ATHROW)
					.max(5, 9)
				);
			cw.method(mw);
		}

		/*
		 * <T> Constructor<T> getConstructor(Class<?> target, Class<?> parameterTypes);
		 */
		{
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();
			MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC | AccessFlag.VARARGS, "getConstructor", MethodType.methodType(Constructor.class, Class.class, Class[].class).toMethodDescriptorString());
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);
			code.instruction(Opcodes.ALOAD_0);
			code.instruction(Opcodes.ALOAD_1);
			code.method(Opcodes.INVOKESPECIAL, className, "getConstructors", MethodType.methodType(Constructor[].class, Class.class).toMethodDescriptorString(), false);
			code.instruction(Opcodes.ASTORE_3)
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
				.mark(m3);
			code.type(Opcodes.NEW, Generator.getType(NoSuchMethodException.class));
			code.instruction(Opcodes.DUP);
			code.type(Opcodes.NEW, Generator.getType(StringBuilder.class));
			code.instruction(Opcodes.DUP);
			code.method(Opcodes.INVOKESPECIAL, Generator.getType(StringBuilder.class), "<init>", "()V", false);
			code.instruction(Opcodes.ALOAD_1);
			code.method(Opcodes.INVOKESPECIAL, Generator.getType(Class.class), "getTypeName", "()Ljava/lang/String;", false);
			code.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
			code.constant(".<init>:");
			code.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
			code.field(Opcodes.GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
			code.instruction(Opcodes.ALOAD_2)
				.method(Opcodes.INVOKESTATIC, Generator.getType(MethodType.class), "methodType", MethodType.methodType(MethodType.class, Class.class, Class[].class).toMethodDescriptorString(), false)
				.method(Opcodes.INVOKEVIRTUAL, Generator.getType(MethodType.class), "toString", MethodType.methodType(String.class).toMethodDescriptorString(), false);
			code.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "append", MethodType.methodType(StringBuilder.class, String.class).toMethodDescriptorString(), false);
			code.method(Opcodes.INVOKEVIRTUAL, Generator.getType(StringBuilder.class), "toString", "()Ljava/lang/String;", false);
			code.method(Opcodes.INVOKESPECIAL, Generator.getType(NoSuchMethodException.class), "<init>", "(Ljava/lang/String;)V", false);
			code.instruction(Opcodes.ATHROW);
			code.max(5, 7);
		}

		/*
		 * Field[] getFields(Class<?>);
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, "getFields", MethodType.methodType(Field[].class, Class.class).toMethodDescriptorString());
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);
			code.instruction(Opcodes.ALOAD_1);
			if (!openJ9VM)
			{
				code.instruction(Opcodes.ICONST_0);
			}
			code.method(Opcodes.INVOKESPECIAL, Generator.getType(Class.class), openJ9VM ? "getDeclaredFieldsImpl" : "getDeclaredFields0", MethodType.methodType(Field[].class, openJ9VM ? new Class<?>[0] : new Class<?>[]{boolean.class}).toMethodDescriptorString(), false);
			code.instruction(Opcodes.ARETURN);
			code.max(2, 2);
		}

		/*
		 * Method[] getMethods(Class<?>);
		 */
		{
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();
			Marker m4 = new Marker();
			MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, "getMethods", MethodType.methodType(Method[].class, Class.class).toMethodDescriptorString());
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);

			code.instruction(Opcodes.ALOAD_1);
			if (!openJ9VM)
			{
				code.instruction(Opcodes.ICONST_0);
			}
			code.method(Opcodes.INVOKESPECIAL, Generator.getType(Class.class), openJ9VM ? "getDeclaredMethodsImpl" :"getDeclaredMethods0", MethodType.methodType(Method[].class, openJ9VM ? new Class<?>[0] : new Class<?>[]{boolean.class}).toMethodDescriptorString(), false)
				.instruction(Opcodes.ASTORE_2)
				.instruction(Opcodes.ALOAD_0)
				.instruction(Opcodes.ALOAD_1)
				.method(Opcodes.INVOKEVIRTUAL, className, "getConstructors", MethodType.methodType(Constructor[].class, Class.class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ASTORE_3)
				.instruction(Opcodes.ALOAD_2)
				.instruction(Opcodes.ARRAYLENGTH)
				.instruction(Opcodes.ALOAD_3)
				.instruction(Opcodes.ARRAYLENGTH)
				.instruction(Opcodes.IADD)
				.type(Opcodes.ANEWARRAY, Generator.getType(Method.class))
				.variable(Opcodes.ASTORE, 4)
				.instruction(Opcodes.ALOAD_2)
				.instruction(Opcodes.ICONST_0)
				.variable(Opcodes.ALOAD, 4)
				.instruction(Opcodes.ICONST_0)
				.instruction(Opcodes.ALOAD_2)
				.instruction(Opcodes.ARRAYLENGTH)
				.method(Opcodes.INVOKESTATIC, Generator.getType(System.class), "arraycopy", MethodType.methodType(void.class, Object.class, int.class, Object.class, int.class, int.class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ICONST_0)
				.variable(Opcodes.ISTORE, 5)
				.mark(m1)
				.variable(Opcodes.ILOAD, 5)
				.instruction(Opcodes.ALOAD_3)
				.instruction(Opcodes.ARRAYLENGTH)
				.jump(Opcodes.IF_ICMPEQ, m4)
				.variable(Opcodes.ALOAD, 4)
				.instruction(Opcodes.ALOAD_2)
				.instruction(Opcodes.ARRAYLENGTH)
				.variable(Opcodes.ILOAD, 5)
				.instruction(Opcodes.IADD)
				.type(Opcodes.NEW, "java/lang/reflect/Method")
				.instruction(Opcodes.DUP)
				.instruction(Opcodes.ALOAD_1)
				.constant("<init>")
				.instruction(Opcodes.ALOAD_3)
				.variable(Opcodes.ILOAD, 5)
				.instruction(Opcodes.AALOAD)
				.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Constructor.class), "getParameterTypes", MethodType.methodType(Class[].class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ALOAD_1)
				.instruction(Opcodes.ICONST_0)
				.type(Opcodes.ANEWARRAY, Generator.getType(Class.class))
				.method(Opcodes.INVOKESTATIC, Generator.getType(MethodType.class), "methodType", MethodType.methodType(MethodType.class, Class.class, Class[].class).toMethodDescriptorString(), false)
				.method(Opcodes.INVOKEVIRTUAL, Generator.getType(MethodType.class), "toMethodDescriptorString", MethodType.methodType(String.class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ICONST_2)
				.method(Opcodes.INVOKEVIRTUAL, Generator.getType(String.class), "substring", MethodType.methodType(String.class, int.class).toMethodDescriptorString(), false)
				.constant("Q")
				.method(Opcodes.INVOKEVIRTUAL, Generator.getType(String.class), "startsWith", MethodType.methodType(boolean.class, String.class).toMethodDescriptorString(), false)
				.jump(Opcodes.IFEQ, m2)
				.instruction(Opcodes.ALOAD_1)
				.jump(Opcodes.GOTO, m3)
				.mark(m2)
				.field(Opcodes.GETSTATIC, Generator.getType(Void.class), "TYPE", Generator.getSignature(Class.class))
				.mark(m3)
				.instruction(Opcodes.ALOAD_3)
				.variable(Opcodes.ILOAD, 5)
				.instruction(Opcodes.AALOAD)
				.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Constructor.class), "getExceptionTypes", MethodType.methodType(Class[].class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ALOAD_3)
				.variable(Opcodes.ILOAD, 5)
				.instruction(Opcodes.AALOAD)
				.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Constructor.class), "getModifiers", MethodType.methodType(int.class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ALOAD_3)
				.variable(Opcodes.ILOAD, 5)
				.instruction(Opcodes.AALOAD)
				.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Constructor.class), "getSlot", MethodType.methodType(int.class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ALOAD_3)
				.variable(Opcodes.ILOAD, 5)
				.instruction(Opcodes.AALOAD)
				.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Constructor.class), "getSignature", MethodType.methodType(String.class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ALOAD_3)
				.variable(Opcodes.ILOAD, 5)
				.instruction(Opcodes.AALOAD)
				.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Constructor.class), "getAnnotationBytes", MethodType.methodType(byte[].class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ALOAD_3)
				.variable(Opcodes.ILOAD, 5)
				.instruction(Opcodes.AALOAD)
				.method(Opcodes.INVOKEVIRTUAL, Generator.getType(Constructor.class), "getRawParameterAnnotations", MethodType.methodType(byte[].class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ACONST_NULL)
				.method(
					Opcodes.INVOKESPECIAL,
					Generator.getType(Method.class),
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
				).instruction(Opcodes.AASTORE)
				.iinc(5, 1)
				.jump(Opcodes.GOTO, m1)
				.mark(m4)
				.variable(Opcodes.ALOAD, 4)
				.instruction(Opcodes.ARETURN);
			code.max(15, 6);
		}

		/*
		 * <T> Constructor<T>[] getConstructors(Class<?> target);
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, "getConstructors", MethodType.methodType(Constructor[].class, Class.class).toMethodDescriptorString());
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);
			code.instruction(Opcodes.ALOAD_1);
			if (!openJ9VM)
			{
				code.instruction(Opcodes.ICONST_0);
			}
			code.method(Opcodes.INVOKESPECIAL, Generator.getType(Class.class), openJ9VM ? "getDeclaredConstructorsImpl" :"getDeclaredConstructors0", MethodType.methodType(Constructor[].class, openJ9VM ? new Class<?>[0] : new Class<?>[]{boolean.class}).toMethodDescriptorString(), false);
			code.instruction(Opcodes.ARETURN);
			code.max(2, 2);
		}

		/*
		 * void throwException(Throwable t);
		 */
		{
			MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, "throwException", "(Ljava/lang/Throwable;)V");
			cw.method(mw);
			CodeWriter code = new CodeWriter();
			mw.attribute(code);
			code.instruction(Opcodes.ALOAD_1);
			code.instruction(Opcodes.ATHROW);
			code.max(1, 2);
		}

		/*
		 * void initialize(Object obj);
		 */
		{
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.PUBLIC, "initialize", MethodType.methodType(void.class, Object.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKESPECIAL, Generator.getType(Object.class), "<init>", "()V", false)
					.instruction(Opcodes.RETURN)
					.max(1, 2)
				);
			cw.method(mw);
		}

		/*
		 * String getName(Member member);
		 */
		{
			MethodHandles.Lookup lookup = ReflectionFactory.TRUSTED_LOOKUP;
			MethodHandle getter = lookup.findStaticGetter(Class.forName(vmVersion == 0x34 ? "sun.reflect.Reflection" : "jdk.internal.reflect.Reflection"), "fieldFilterMap", Map.class);
			MethodHandle setter = lookup.findStaticSetter(Class.forName(vmVersion == 0x34 ? "sun.reflect.Reflection" : "jdk.internal.reflect.Reflection"), "fieldFilterMap", Map.class);
			Map<?, ?> filter = (Map<?, ?>) getter.invoke();
			setter.invoke((Object) null);
			Marker m1 = new Marker();
			Marker m2 = new Marker();
			Marker m3 = new Marker();
			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.PUBLIC, "getName", MethodType.methodType(String.class, Member.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.instruction(Opcodes.ALOAD_1)
					.type(Opcodes.INSTANCEOF, Generator.getType(Method.class))
					.jump(Opcodes.IFEQ, m1)
					.field(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
					.instruction(Opcodes.ALOAD_1)
					.constant(ReflectionFactory.UNSAFE.objectFieldOffset(Method.class.getDeclaredField("name")))
					.method(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "getObject", MethodType.methodType(Object.class, Object.class, long.class).toMethodDescriptorString(), true)
					.type(Opcodes.CHECKCAST, Generator.getType(String.class))
					.instruction(Opcodes.ARETURN)
					.mark(m1)
					.instruction(Opcodes.ALOAD_1)
					.type(Opcodes.INSTANCEOF, Generator.getType(Field.class))
					.jump(Opcodes.IFEQ, m2)
					.field(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
					.instruction(Opcodes.ALOAD_1)
					.constant(ReflectionFactory.UNSAFE.objectFieldOffset(Field.class.getDeclaredField("name")))
					.method(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "getObject", MethodType.methodType(Object.class, Object.class, long.class).toMethodDescriptorString(), true)
					.type(Opcodes.CHECKCAST, Generator.getType(String.class))
					.instruction(Opcodes.ARETURN)
					.mark(m2)
					.instruction(Opcodes.ALOAD_1)
					.type(Opcodes.INSTANCEOF, Generator.getType(Constructor.class))
					.jump(Opcodes.IFEQ, m3)
					.constant("<init>")
					.instruction(Opcodes.ARETURN)
					.mark(m3)
					.instruction(Opcodes.ALOAD_1)
					.method(Opcodes.INVOKEINTERFACE, Generator.getType(Member.class), "getName", MethodType.methodType(String.class).toMethodDescriptorString(), true)
					.instruction(Opcodes.ARETURN)
					.max(4, 2)
					.attribute(new StackMapTableWriter()
						.sameFrame(m1)
						.sameFrame(m2)
						.sameFrame(m3)
					)
				);
			cw.method(mw);
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
					Class.forName("java.lang.J9VMInternals");
					openJ9VM = true;
				}
				catch (Throwable t)
				{
					openJ9VM = false;
				}

				if (openJ9VM)
				{
					pid = (int) (long) lookup.findVirtual(
						Class.forName("com.ibm.lang.management.internal.ExtendedRuntimeMXBeanImpl"),
						"getProcessIDImpl",
						MethodType.methodType(long.class)
					).invoke(ManagementFactory.getRuntimeMXBean());
				}
				else
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
			}

			MethodWriter mw = new MethodWriter()
				.set(AccessFlag.PUBLIC, "getPID", MethodType.methodType(int.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.constant(pid)
					.instruction(Opcodes.IRETURN)
					.max(1, 1)
				);
			cw.method(mw);
		}

		return cw;
	}
}
