package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.file.AccessFlag;

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
		MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		this.bytecode.addMethod(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.addAttribute(code);
		code.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.addFieldInstruction(Opcodes.GETSTATIC, this.bytecode.getName(), "1", Generator.getSignature(AccessibleObject.class))
			.addTypeInstruction(Opcodes.CHECKCAST, Generator.getType(Method.class));
		if (statics)
		{
			code.addFieldInstruction(Opcodes.GETSTATIC, this.bytecode.getName(), "0", Generator.getSignature(Class.class));
		}
		else
		{
			code.addInstruction(Opcodes.ALOAD_1)
				.addInstruction(Opcodes.ICONST_0)
				.addInstruction(Opcodes.AALOAD);
		}
		code.addInstruction(Opcodes.ALOAD_1)
			.addInstruction(statics ? Opcodes.ICONST_0 : Opcodes.ICONST_1)
			.addInstruction(Opcodes.ALOAD_1)
			.addInstruction(Opcodes.ARRAYLENGTH)
			.addMethodInstruction(Opcodes.INVOKESTATIC, getType(Arrays.class), "copyOfRange", MethodType.methodType(Object[].class, Object[].class, int.class, int.class).toMethodDescriptorString(), false)
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "invoke", MethodType.methodType(Object.class, Method.class, Object.class, Object[].class).toMethodDescriptorString(), true)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(6, 2);
		if (statics && this.method.getParameterTypes().length == 0)
		{
			mw = new MethodWriter()
				.set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString())
				.addAttribute(new CodeWriter()
					.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
					.addFieldInstruction(Opcodes.GETSTATIC, this.bytecode.getName(), "1", Generator.getSignature(AccessibleObject.class))
					.addTypeInstruction(Opcodes.CHECKCAST, Generator.getType(Method.class))
					.addFieldInstruction(Opcodes.GETSTATIC, this.bytecode.getName(), "0", Generator.getSignature(Class.class))
					.addInstruction(Opcodes.ICONST_0)
					.addTypeInstruction(Opcodes.ANEWARRAY, Generator.getType(Object.class))
					.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "invoke", MethodType.methodType(Object.class, Method.class, Object.class, Object[].class).toMethodDescriptorString(), true)
					.addInstruction(Opcodes.ARETURN)
					.setMaxs(4, 1)
				);
			Generator.inline(mw);
			this.bytecode.addMethod(mw);
		}
	}
}
