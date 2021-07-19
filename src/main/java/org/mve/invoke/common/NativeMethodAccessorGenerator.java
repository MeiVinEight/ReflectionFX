package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.AccessFlag;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class NativeMethodAccessorGenerator extends MethodAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Method method = this.getMethod();

	public NativeMethodAccessorGenerator(Method method, int kind)
	{
		super(method, kind);
	}

	@Override
	public void generate()
	{
		int modifiers = this.method.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		this.bytecode.method(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.attribute(code);
		code.field(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.field(Opcodes.GETSTATIC, this.bytecode.name, "1", Generator.getSignature(AccessibleObject.class))
			.type(Opcodes.CHECKCAST, Generator.getType(Method.class));
		if (statics)
		{
			code.field(Opcodes.GETSTATIC, this.bytecode.name, "0", Generator.getSignature(Class.class));
		}
		else
		{
			code.instruction(Opcodes.ALOAD_1)
				.instruction(Opcodes.ICONST_0)
				.instruction(Opcodes.AALOAD);
		}
		code.instruction(Opcodes.ALOAD_1)
			.instruction(statics ? Opcodes.ICONST_0 : Opcodes.ICONST_1)
			.instruction(Opcodes.ALOAD_1)
			.instruction(Opcodes.ARRAYLENGTH)
			.method(Opcodes.INVOKESTATIC, getType(Arrays.class), "copyOfRange", MethodType.methodType(Object[].class, Object[].class, int.class, int.class).toMethodDescriptorString(), false)
			.method(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "invoke", MethodType.methodType(Object.class, Method.class, Object.class, Object[].class).toMethodDescriptorString(), true)
			.instruction(Opcodes.ARETURN)
			.max(6, 2);
		if (statics && this.method.getParameterTypes().length == 0)
		{
			mw = new MethodWriter()
				.set(AccessFlag.PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString())
				.attribute(new CodeWriter()
					.field(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
					.field(Opcodes.GETSTATIC, this.bytecode.name, "1", Generator.getSignature(AccessibleObject.class))
					.type(Opcodes.CHECKCAST, Generator.getType(Method.class))
					.field(Opcodes.GETSTATIC, this.bytecode.name, "0", Generator.getSignature(Class.class))
					.instruction(Opcodes.ICONST_0)
					.type(Opcodes.ANEWARRAY, Generator.getType(Object.class))
					.method(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "invoke", MethodType.methodType(Object.class, Method.class, Object.class, Object[].class).toMethodDescriptorString(), true)
					.instruction(Opcodes.ARETURN)
					.max(4, 1)
				);
			Generator.inline(mw);
			this.bytecode.method(mw);
		}
	}
}
