package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;
import org.mve.invoke.MethodAccessor;

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
		this.bytecode().interfaces = new String[]{Generator.getType(MethodAccessor.class)};
	}

	public void pregenerate(ClassWriter bytecode)
	{
		super.pregenerate(bytecode);
		bytecode.method(new MethodWriter()
			.set(AccessFlag.PUBLIC, "getMethod", MethodType.methodType(Method.class).toMethodDescriptorString())
			.attribute(new CodeWriter()
				.field(Opcodes.GETSTATIC, bytecode.name, "1", Generator.getSignature(AccessibleObject.class))
				.type(Opcodes.CHECKCAST, Generator.getType(Method.class))
				.instruction(Opcodes.ARETURN)
				.max(1, 1)
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
