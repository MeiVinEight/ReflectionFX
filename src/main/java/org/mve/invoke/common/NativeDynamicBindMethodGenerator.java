package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;
import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class NativeDynamicBindMethodGenerator extends DynamicBindMethodGenerator
{
	public NativeDynamicBindMethodGenerator(Class<?> target, MethodKind implementation, MethodKind invocation, int kind)
	{
		super(target, implementation, invocation, kind);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		boolean statics = this.kind() == ReflectionFactory.KIND_INVOKE_STATIC;
		MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, this.implementation().name(), this.implementation().type().toMethodDescriptorString());
		bytecode.method(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.attribute(code);
		code.field(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.field(Opcodes.GETSTATIC, bytecode.name, this.invocation().name().concat(this.invocation().type().toMethodDescriptorString()), Generator.getSignature(Method.class));
		if (statics)
		{
			code.field(Opcodes.GETSTATIC, bytecode.name, "0", Generator.getSignature(Class.class));
		}
		else
		{
			code.instruction(Opcodes.ALOAD_1);
		}
		code.number(Opcodes.BIPUSH, invocation().type().parameterArray().length)
			.type(Opcodes.ANEWARRAY, Generator.getType(Class.class));
		int args = 0;
		int local = statics ? 1 : 2;
		Class<?>[] parameters = this.invocation().type().parameterArray();
		for (Class<?> parameterType : parameters)
		{
			code.instruction(Opcodes.DUP)
				.number(Opcodes.BIPUSH, args++);
			if (integer(parameterType)) code.variable(Opcodes.ILOAD, local);
			else if (parameterType == long.class) code.variable(Opcodes.LLOAD, local);
			else if (parameterType == float.class) code.variable(Opcodes.FLOAD, local);
			else if (parameterType == double.class) code.variable(Opcodes.DLOAD, local);
			else code.variable(Opcodes.ALOAD, local);
			local += Generator.typeSize(parameterType);
			Generator.warp(parameterType, code);
			code.instruction(Opcodes.AASTORE);
		}
		code.method(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "invoke", MethodType.methodType(Object.class, Method.class, Object.class, Object[].class).toMethodDescriptorString(), true);
		Class<?> returnType = this.invocation().type().returnType();
		if (returnType == void.class)
		{
			code.instruction(Opcodes.POP)
				.instruction(Opcodes.RETURN);
		}
		else if (returnType.isPrimitive())
		{
			Generator.unwarp(returnType, code);
		}
		Generator.returner(returnType, code);
		code.max(invocation().type().parameterArray().length == 0 ? 4 : 8, local);
	}
}
