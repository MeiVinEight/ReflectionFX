package org.mve.invoke.common;

import org.mve.asm.AccessFlag;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ReflectionAccessor;

import java.lang.invoke.MethodType;

public abstract class AllocatorGenerator extends AccessorGenerator
{
	public AllocatorGenerator(Class<?> target, Object[] argument)
	{
		super(target, argument);
		this.bytecode().interfaces = new String[]{Generator.type(ReflectionAccessor.class)};
	}

	@Override
	public void generate()
	{
		this.bytecode().method(new MethodWriter()
			.set(AccessFlag.PUBLIC, ReflectionAccessor.WITH, MethodType.methodType(ReflectionAccessor.class, Object[].class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.instruction(Opcodes.ALOAD_0)
				.instruction(Opcodes.ARETURN)
				.max(1, 2)
			)
		);
	}
}
