package org.mve.invoke.common;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ConstructorAccessor;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;

public abstract class ConstructorAccessorGenerator extends AccessibleObjectAccessorGenerator
{
	private final Constructor<?> constructor;
	private final int argument;

	public ConstructorAccessorGenerator(Constructor<?> ctr, Object[] argument)
	{
		super(ctr, ctr.getDeclaringClass(), argument);
		this.constructor = ctr;
		this.argument = argument.length;
		this.bytecode().interfaces = new String[]{Generator.type(ConstructorAccessor.class)};
	}

	public void pregenerate(ClassWriter bytecode)
	{
		super.pregenerate(bytecode);
		bytecode.method(new MethodWriter()
			.set(AccessFlag.PUBLIC, ConstructorAccessor.CONSTRUCTOR, MethodType.methodType(Constructor.class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, bytecode.name, Generator.CONSTANT_POOL[5], Generator.signature(AccessibleObject.class))
				.type(Opcodes.CHECKCAST, Generator.type(Constructor.class))
				.instruction(Opcodes.ARETURN)
				.max(1, 1)
			)
		);
	}

	public Constructor<?> getConstructor()
	{
		return constructor;
	}

	@Override
	public void generate()
	{
		super.generate();
		Generator.with(this.bytecode(), ConstructorAccessor.class, Constructor.class, this.argument);
	}
}
