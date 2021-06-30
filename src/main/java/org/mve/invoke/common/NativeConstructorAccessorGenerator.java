package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;

public class NativeConstructorAccessorGenerator extends ConstructorAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();

	public NativeConstructorAccessorGenerator(Constructor<?> ctr)
	{
		super(ctr);
	}

	@Override
	public void generate()
	{
		Constructor<?> constructor = this.getConstructor();
		MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		this.bytecode.method(mw);
		Generator.inline(mw);
		mw.attribute(new CodeWriter()
			.field(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.field(Opcodes.GETSTATIC, this.bytecode.name, "1", Generator.getSignature(AccessibleObject.class))
			.type(Opcodes.CHECKCAST, Generator.getType(Constructor.class))
			.instruction(Opcodes.ALOAD_1)
			.method(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "construct", MethodType.methodType(Object.class, Constructor.class, Object[].class).toMethodDescriptorString(), true)
			.instruction(Opcodes.ARETURN)
			.max(3, 2)
		);
		if (constructor.getParameterTypes().length == 0)
		{
			mw = new MethodWriter().set(AccessFlag.PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString());
			this.bytecode.method(mw);
			Generator.inline(mw);
			mw.attribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
				.field(Opcodes.GETSTATIC, this.bytecode.name, "1", Generator.getSignature(AccessibleObject.class))
				.type(Opcodes.CHECKCAST, Generator.getType(Constructor.class))
				.instruction(Opcodes.ICONST_0)
				.type(Opcodes.ANEWARRAY, Generator.getType(Object.class))
				.method(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "construct", MethodType.methodType(Object.class, Constructor.class, Object[].class).toMethodDescriptorString(), true)
				.instruction(Opcodes.ARETURN)
				.max(3, 1)
			);
		}
	}
}
