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
		bytecode.addMethod(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.addAttribute(code);
		code.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.addFieldInstruction(Opcodes.GETSTATIC, bytecode.getName(), this.invocation().name().concat(this.invocation().type().toMethodDescriptorString()), Generator.getSignature(Method.class));
		if (statics)
		{
			code.addFieldInstruction(Opcodes.GETSTATIC, bytecode.getName(), "0", Generator.getSignature(Class.class));
		}
		else
		{
			code.addInstruction(Opcodes.ALOAD_1);
		}
		code.addNumberInstruction(Opcodes.BIPUSH, invocation().type().parameterArray().length)
			.addTypeInstruction(Opcodes.ANEWARRAY, Generator.getType(Class.class));
		int args = 0;
		int local = statics ? 1 : 2;
		Class<?>[] parameters = this.invocation().type().parameterArray();
		for (Class<?> parameterType : parameters)
		{
			code.addInstruction(Opcodes.DUP)
				.addNumberInstruction(Opcodes.BIPUSH, args++);
			if (integer(parameterType)) code.addLocalVariableInstruction(Opcodes.ILOAD, local);
			else if (parameterType == long.class) code.addLocalVariableInstruction(Opcodes.LLOAD, local);
			else if (parameterType == float.class) code.addLocalVariableInstruction(Opcodes.FLOAD, local);
			else if (parameterType == double.class) code.addLocalVariableInstruction(Opcodes.DLOAD, local);
			else code.addLocalVariableInstruction(Opcodes.ALOAD, local);
			local += Generator.typeSize(parameterType);
			Generator.warp(parameterType, code);
			code.addInstruction(Opcodes.AASTORE);
		}
		code.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "invoke", MethodType.methodType(Object.class, Method.class, Object.class, Object[].class).toMethodDescriptorString(), true);
		Class<?> returnType = this.invocation().type().returnType();
		if (returnType == void.class)
		{
			code.addInstruction(Opcodes.POP)
				.addInstruction(Opcodes.RETURN);
		}
		else if (returnType.isPrimitive())
		{
			Generator.unwarp(returnType, code);
		}
		Generator.returner(returnType, code);
		code.setMaxs(invocation().type().parameterArray().length == 0 ? 4 : 8, local);
	}
}
