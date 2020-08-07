package org.mve.invoke;

import org.mve.util.asm.ClassWriter;
import org.mve.util.asm.Opcodes;
import org.mve.util.asm.file.AccessFlag;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;

public abstract class ConstructorAccessorGenerator extends AccessibleObjectAccessorGenerator
{
	private final ClassWriter bytecode = this.bytecode();
	private final Constructor<?> constructor;

	public ConstructorAccessorGenerator(Constructor<?> ctr)
	{
		super(ctr, ctr.getDeclaringClass());
		this.constructor = ctr;
		this.bytecode.setInterfaces(new String[]{Generator.getType(ConstructorAccessor.class)});
	}

	public void pregenerate(ClassWriter bytecode)
	{
		super.pregenerate(bytecode);
		bytecode.addMethod(AccessFlag.ACC_PUBLIC, "getConstructor", MethodType.methodType(Constructor.class).toMethodDescriptorString()).addCode()
			.addFieldInstruction(Opcodes.GETSTATIC, bytecode.getName(), "1", Generator.getSignature(AccessibleObject.class))
			.addTypeInstruction(Opcodes.CHECKCAST, Generator.getType(Constructor.class))
			.addInstruction(Opcodes.ARETURN)
			.setMaxs(1, 1);
	}

	public Constructor<?> getConstructor()
	{
		return constructor;
	}
}
