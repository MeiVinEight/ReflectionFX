package org.mve.invoke;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;

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
		MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class, Object[].class).toMethodDescriptorString());
		this.bytecode.addMethod(mw);
		Generator.inline(mw);
		mw.addAttribute(new CodeWriter()
			.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
			.addFieldInstruction(Opcodes.GETSTATIC, this.bytecode.getName(), "1", Generator.getSignature(AccessibleObject.class))
			.addTypeInstruction(Opcodes.CHECKCAST, Generator.getType(Constructor.class))
			.addInstruction(Opcodes.ALOAD_1)
			.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "construct", MethodType.methodType(Object.class, Constructor.class, Object[].class).toMethodDescriptorString(), true)
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(3, 2)
		);
		if (constructor.getParameterTypes().length == 0)
		{
			mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, "invoke", MethodType.methodType(Object.class).toMethodDescriptorString());
			this.bytecode.addMethod(mw);
			Generator.inline(mw);
			mw.addAttribute(new CodeWriter()
				.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class))
				.addFieldInstruction(Opcodes.GETSTATIC, this.bytecode.getName(), "1", Generator.getSignature(AccessibleObject.class))
				.addTypeInstruction(Opcodes.CHECKCAST, Generator.getType(Constructor.class))
				.addInstruction(Opcodes.ICONST_0)
				.addTypeInstruction(Opcodes.ANEWARRAY, Generator.getType(Object.class))
				.addMethodInstruction(Opcodes.INVOKEINTERFACE, Generator.getType(Unsafe.class), "construct", MethodType.methodType(Object.class, Constructor.class, Object[].class).toMethodDescriptorString(), true)
				.addInstruction(Opcodes.ARETURN)
				.setMaxs(3, 1)
			);
		}
	}
}
