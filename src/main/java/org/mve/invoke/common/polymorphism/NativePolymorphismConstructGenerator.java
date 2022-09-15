package org.mve.invoke.common.polymorphism;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;
import org.mve.invoke.common.Generator;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;

public class NativePolymorphismConstructGenerator extends PolymorphismConstructGenerator
{
	public NativePolymorphismConstructGenerator(Class<?> target, MethodKind implementation, MethodKind invocation)
	{
		super(target, implementation, invocation);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString());
		bytecode.method(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.attribute(code);
		code.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
			.field(Opcodes.GETSTATIC, bytecode.name, "<init>".concat(MethodType.methodType(void.class, invocation().type().parameterArray()).toMethodDescriptorString()), Generator.signature(Constructor.class))
			.number(Opcodes.BIPUSH, invocation().type().parameterArray().length)
			.type(Opcodes.ANEWARRAY, Generator.type(Class.class));
		int args = 0;
		int local = 1;
		Class<?>[] parameters = invocation().type().parameterArray();
		for (Class<?> parameterType : parameters)
		{
			code.instruction(Opcodes.DUP)
				.number(Opcodes.BIPUSH, args++);
			Generator.load(parameterType, code, local);
			local += Generator.typeSize(parameterType);
			Generator.warp(parameterType, code);
			code.instruction(Opcodes.AASTORE);
		}
		code.method(Opcodes.INVOKEINTERFACE, Generator.type(Unsafe.class), "construct", MethodType.methodType(Object.class, Constructor.class, Object[].class).toMethodDescriptorString(), true)
			.instruction(Opcodes.ARETURN)
			.max(parameters.length == 0 ? 3 : 7, local);
	}
}
