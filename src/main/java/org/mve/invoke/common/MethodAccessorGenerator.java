package org.mve.invoke;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;

import java.lang.invoke.MethodType;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

public abstract class MethodAccessorGenerator extends AccessibleObjectAccessorGenerator
{
	private final Method method;
	private final int kind;

	public MethodAccessorGenerator(Method method, int kind)
	{
		super(method, method.getDeclaringClass());
		this.method = method;
		this.kind = kind;
		this.bytecode().setInterfaces(new String[]{Generator.getType(MethodAccessor.class)});
	}

	public void pregenerate(ClassWriter bytecode)
	{
		super.pregenerate(bytecode);
		bytecode.addMethod(new MethodWriter()
			.set(AccessFlag.ACC_PUBLIC, "getMethod", MethodType.methodType(Method.class).toMethodDescriptorString())
			.addAttribute(new CodeWriter()
				.addFieldInstruction(Opcodes.GETSTATIC, bytecode.getName(), "1", Generator.getSignature(AccessibleObject.class))
				.addTypeInstruction(Opcodes.CHECKCAST, Generator.getType(Method.class))
				.addInstruction(Opcodes.ARETURN)
				.setMaxs(1, 1)
			)
		);
	}

	public Method getMethod()
	{
		return this.method;
	}

	public int kind()
	{
		return this.kind;
	}
}
