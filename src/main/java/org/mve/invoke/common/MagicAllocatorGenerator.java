package org.mve.invoke.common;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ReflectionAccessor;

import java.lang.invoke.MethodType;

public class MagicAllocatorGenerator extends AllocatorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Class<?> target;

	public MagicAllocatorGenerator(Class<?> target, Object[] argument)
	{
		super(target, argument);
		this.target = target;
	}

	@Override
	public void generate()
	{
		super.generate();
		MethodWriter mw = new MethodWriter()
			.set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.type(Opcodes.NEW, Generator.type(this.target))
				.instruction(Opcodes.ARETURN)
				.max(1, 2)
			);
		Generator.inline(mw);
		this.bytecode.method(mw);

		mw = new MethodWriter()
		.set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class).toMethodDescriptorString())
		.attribute(new CodeWriter()
			.type(Opcodes.NEW, Generator.type(this.target))
			.instruction(Opcodes.ARETURN)
			.max(1, 1)
		);
		Generator.inline(mw);
		this.bytecode.method(mw);
	}
}
