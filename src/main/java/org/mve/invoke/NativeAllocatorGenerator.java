package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.MethodWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.attribute.CodeWriter;
import org.mve.util.asm.file.AccessFlag;

import java.lang.invoke.MethodType;

public class NativeAllocatorGenerator extends AllocatorGenerator
{
	private final ClassWriter bytecode = this.bytecode();

	public NativeAllocatorGenerator(Class<?> target)
	{
		super(target);
	}

	@Override
	public void generate()
	{
		MethodWriter mw = new MethodWriter()
			.set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString())
			.addAttribute(new CodeWriter()
				.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
				.addFieldInstruction(Opcodes.GETSTATIC, this.bytecode.getName(), "0", Generator.getSignature(Class.class))
				.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "allocateInstance", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString(), true)
				.addInstruction(Opcodes.ARETURN)
				.setMaxs(1, 2)
			);
		Generator.inline(mw);
		this.bytecode.addMethod(mw);

		mw = new MethodWriter()
		.set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString())
		.addAttribute(new CodeWriter()
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.addFieldInstruction(Opcodes.GETSTATIC, this.bytecode.getName(), "0", Generator.getSignature(Class.class))
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "allocateInstance", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString(), true)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 1)
		);
		Generator.inline(mw);
		this.bytecode.addMethod(mw);
	}
}
