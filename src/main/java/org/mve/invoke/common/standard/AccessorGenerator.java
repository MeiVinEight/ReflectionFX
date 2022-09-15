package org.mve.invoke.common.standard;

import org.mve.asm.AccessFlag;
import org.mve.asm.ClassWriter;
import org.mve.asm.FieldWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.MagicAccessor;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.JavaVM;

import java.lang.invoke.MethodType;

public abstract class AccessorGenerator extends Generator
{
	private static final Unsafe UNSAFE = ReflectionFactory.UNSAFE;
	private static final MagicAccessor ACCESSOR = ReflectionFactory.ACCESSOR;
	private final ClassWriter bytecode = new ClassWriter();
	private final Class<?> target;
	private final Object[] argument;

	public AccessorGenerator(Class<?> target, Object[] argument)
	{
		this.target = target;
		this.argument = argument;
		this.bytecode.set(Opcodes.version(8), AccessFlag.PUBLIC | AccessFlag.SUPER, JavaVM.random(), JavaVM.CONSTANT[JavaVM.CONSTANT_MAGIC]);
		this.pregenerate(this.bytecode);
	}

	public void pregenerate(ClassWriter bytecode)
	{
		bytecode.field(new FieldWriter()
			.set(AccessFlag.PUBLIC | AccessFlag.FINAL | AccessFlag.STATIC, JavaVM.CONSTANT[ReflectionAccessor.FIELD_CLASS], Generator.signature(Class.class))
		).field(new FieldWriter()
			.set(AccessFlag.PUBLIC | AccessFlag.FINAL | AccessFlag.STATIC, JavaVM.CONSTANT[ReflectionAccessor.FIELD_WITH], Generator.signature(Object[].class))
		).method(new MethodWriter()
			.set(AccessFlag.PUBLIC, ReflectionAccessor.OBJECTIVE, MethodType.methodType(Class.class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, this.bytecode.name, JavaVM.CONSTANT[ReflectionAccessor.FIELD_CLASS], Generator.signature(Class.class))
				.instruction(Opcodes.ARETURN)
				.max(1, 1)
			)
		).method(new MethodWriter()
			.set(AccessFlag.PUBLIC, ReflectionAccessor.ARGUMENT, MethodType.methodType(Object[].class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, this.bytecode.name, JavaVM.CONSTANT[ReflectionAccessor.FIELD_WITH], Generator.signature(Object[].class))
				.instruction(Opcodes.ARETURN)
				.max(1, 1)
			)
		);
	}

	public void postgenerate(Class<?> generated)
	{
		UNSAFE.putObject(generated, UNSAFE.staticFieldOffset(ACCESSOR.getField(generated, JavaVM.CONSTANT[ReflectionAccessor.FIELD_CLASS])), target);
		UNSAFE.putObject(generated, UNSAFE.staticFieldOffset(ACCESSOR.getField(generated, JavaVM.CONSTANT[ReflectionAccessor.FIELD_WITH])), this.argument);
	}

	public ClassWriter bytecode()
	{
		return this.bytecode;
	}

	public void generate()
	{
		MethodWriter mw;
		bytecode.method(mw = new MethodWriter()
			.set(AccessFlag.PUBLIC, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.instruction(Opcodes.ALOAD_0)
				.instruction(Opcodes.ICONST_0)
				.type(Opcodes.ANEWARRAY, Generator.type(Object.class))
				.method(Opcodes.INVOKEVIRTUAL, this.bytecode.name, ReflectionAccessor.INVOKE, MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString(), false)
				.instruction(Opcodes.ARETURN)
				.max(2, 1)
			)
		);
		Generator.inline(mw);
	}
}
