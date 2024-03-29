package org.mve.invoke.common.standard;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.Unsafe;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.JavaVM;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;

public class NativeConstructorAccessorGenerator extends ConstructorAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final int argument;

	public NativeConstructorAccessorGenerator(Constructor<?> ctr, Object[] argument)
	{
		super(ctr, argument);
		this.argument = argument.length;
	}

	@Override
	public void generate()
	{
		super.generate();
		MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		this.bytecode.method(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.attribute(code);
		Generator.merge(code, this.bytecode.name, this.argument);
		code.field(Opcodes.GETSTATIC, Generator.type(Unsafe.class), "unsafe", Generator.signature(Unsafe.class))
			.field(Opcodes.GETSTATIC, this.bytecode.name, JavaVM.CONSTANT[ReflectionAccessor.FIELD_OBJECTIVE], Generator.signature(AccessibleObject.class))
			.type(Opcodes.CHECKCAST, Generator.type(Constructor.class))
			.instruction(Opcodes.ALOAD_1)
			.method(Opcodes.INVOKEVIRTUAL, Generator.type(Unsafe.class), "construct", MethodType.methodType(Object.class, Constructor.class, Object[].class).toMethodDescriptorString(), false)
			.instruction(Opcodes.ARETURN)
			.max(3, 3);
	}
}
