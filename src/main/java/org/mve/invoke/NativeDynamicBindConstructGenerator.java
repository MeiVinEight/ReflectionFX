package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.file.AccessFlag;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;

public class NativeDynamicBindConstructGenerator extends DynamicBindConstructGenerator
{
	public NativeDynamicBindConstructGenerator(Class<?> target, MethodKind implementation, MethodKind invocation)
	{
		super(target, implementation, invocation);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		MethodWriter mw = bytecode.addMethod(AccessFlag.ACC_PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString());
		Generator.inline(mw);
		CodeWriter code = mw.addCode();
		code.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.addFieldInstruction(Opcodes.GETSTATIC, bytecode.getName(), "<init>".concat(MethodType.methodType(void.class, invocation().type().parameterArray()).toMethodDescriptorString()), Generator.getSignature(Constructor.class))
			.addNumberInstruction(Opcodes.BIPUSH, invocation().type().parameterArray().length)
			.addTypeInstruction(Opcodes.ANEWARRAY, Generator.getType(Class.class));
		int args = 0;
		int local = 1;
		Class<?>[] parameters = invocation().type().parameterArray();
		for (Class<?> parameterType : parameters)
		{
			code.addInstruction(Opcodes.DUP)
				.addNumberInstruction(Opcodes.BIPUSH, args++);
			if (Generator.integer(parameterType)) code.addLocalVariableInstruction(Opcodes.ILOAD, local);
			else if (parameterType == long.class) code.addLocalVariableInstruction(Opcodes.LLOAD, local);
			else if (parameterType == float.class) code.addLocalVariableInstruction(Opcodes.FLOAD, local);
			else if (parameterType == double.class) code.addLocalVariableInstruction(Opcodes.DLOAD, local);
			else code.addLocalVariableInstruction(Opcodes.ALOAD, local);
			local += Generator.typeSize(parameterType);
			Generator.warp(parameterType, code);
			code.addInstruction(Opcodes.AASTORE);
		}
		code.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "construct", MethodType.methodType(Object.class, Constructor.class, Object[].class).toMethodDescriptorString(), true)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(parameters.length == 0 ? 3 : 7, local);
	}
}
