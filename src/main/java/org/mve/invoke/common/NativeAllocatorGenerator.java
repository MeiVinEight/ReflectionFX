package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.AccessFlag;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;

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
			.set(AccessFlag.PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
				.field(Opcodes.GETSTATIC, this.bytecode.name, "0", Generator.getSignature(Class.class))
				.method(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "allocateInstance", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString(), true)
				.instruction(Opcodes.ARETURN)
				.max(1, 2)
			);
		Generator.inline(mw);
		this.bytecode.method(mw);

		mw = new MethodWriter()
		.set(AccessFlag.PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString())
		.attribute(new CodeWriter()
			.field(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.field(Opcodes.GETSTATIC, this.bytecode.name, "0", Generator.getSignature(Class.class))
			.method(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "allocateInstance", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString(), true)
			.instruction(Opcodes.ARETURN)
			.max(1, 1)
		);
		Generator.inline(mw);
		this.bytecode.method(mw);
	}
}
