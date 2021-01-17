package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.file.AccessFlag;

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
			.addAttribute(new CodeWriter()
				.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
				.addFieldInstruction(Opcodes.GETSTATIC, bytecode.getName(), "0", Generator.getSignature(Class.class))
				.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "allocateInstance", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString(), true)
				.addInstruction(Opcodes.ARETURN)
				.setMaxs(3, 1 + Generator.parameterSize(implementation().type().parameterArray()))
			);
		Generator.inline(mw);
		bytecode.addMethod(mw);
	}
}
