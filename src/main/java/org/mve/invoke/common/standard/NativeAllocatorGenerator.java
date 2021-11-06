package org.mve.invoke.common.standard;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.JavaVM;

import java.lang.invoke.MethodType;

public class NativeAllocatorGenerator extends AllocatorGenerator
{
	private final ClassWriter bytecode = this.bytecode();

	public NativeAllocatorGenerator(Class<?> target, Object[] argument)
	{
		super(target, argument);
	}

	@Override
	public void generate()
	{
		super.generate();
		MethodWriter mw = new MethodWriter()
			.set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
				.field(Opcodes.GETSTATIC, this.bytecode.name, JavaVM.CONSTANT[4], Generator.signature(Class.class))
				.method(Opcodes.INVOKEINTERFACE, Generator.type(Unsafe.class), "allocateInstance", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString(), true)
				.instruction(Opcodes.ARETURN)
				.max(1, 2)
			);
		Generator.inline(mw);
		this.bytecode.method(mw);

		mw = new MethodWriter()
		.set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class).toMethodDescriptorString())
		.attribute(new CodeWriter()
			.field(Opcodes.GETSTATIC, Generator.type(ReflectionFactory.class), "UNSAFE", Generator.signature(Unsafe.class))
			.field(Opcodes.GETSTATIC, this.bytecode.name, JavaVM.CONSTANT[4], Generator.signature(Class.class))
			.method(Opcodes.INVOKEINTERFACE, Generator.type(Unsafe.class), "allocateInstance", MethodType.methodType(Object.class, Class.class).toMethodDescriptorString(), true)
			.instruction(Opcodes.ARETURN)
			.max(1, 1)
		);
		Generator.inline(mw);
		this.bytecode.method(mw);
	}
}
