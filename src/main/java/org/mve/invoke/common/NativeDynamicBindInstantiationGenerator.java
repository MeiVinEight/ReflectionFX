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

public class NativeDynamicBindInstantiationGenerator extends DynamicBindInstantiationGenerator
{
	public NativeDynamicBindInstantiationGenerator(Class<?> target, MethodKind implementation)
	{
		super(target, implementation);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		MethodWriter mw = new MethodWriter()
			.set(AccessFlag.ACC_PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString())
			.attribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
				.field(Opcodes.GETSTATIC, bytecode.name, "0", Generator.getSignature(Class.class))
				.method(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "allocateInstance", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString(), true)
				.instruction(Opcodes.ARETURN)
				.max(3, 1 + Generator.parameterSize(implementation().type().parameterArray()))
			);
		Generator.inline(mw);
		bytecode.method(mw);
	}
}
