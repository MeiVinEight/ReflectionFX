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

public class NativePolymorphismInstantiationGenerator extends PolymorphismInstantiationGenerator
{
	public NativePolymorphismInstantiationGenerator(Class<?> target, MethodKind implementation)
	{
		super(target, implementation);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		MethodWriter mw = new MethodWriter()
			.set(AccessFlag.PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString())
			.attribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
				.field(Opcodes.GETSTATIC, bytecode.name, "0", Generator.signature(Class.class))
				.method(Opcodes.INVOKEINTERFACE, Generator.type(Unsafe.class), "allocateInstance", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString(), true)
				.instruction(Opcodes.ARETURN)
				.max(3, 1 + Generator.parameterSize(implementation().type().parameterArray()))
			);
		Generator.inline(mw);
		bytecode.method(mw);
	}
}
