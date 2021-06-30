package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;
import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionFactory;

public class MagicDynamicBindMethodGenerator extends DynamicBindMethodGenerator
{
	public MagicDynamicBindMethodGenerator(Class<?> target, MethodKind implementation, MethodKind invocation, int kind)
	{
		super(target, implementation, invocation, kind);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		Class<?> target = this.getTarget();
		MethodKind implementation = this.implementation();
		MethodKind invocation = this.invocation();
		int kind = this.kind();
		MethodWriter mw = new MethodWriter().set(AccessFlag.PUBLIC, implementation.name(), implementation.type().toMethodDescriptorString());
		bytecode.method(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.attribute(code);
		int local = 1;
		Class<?>[] params = implementation.type().parameterArray();
		for (Class<?> c : params)
		{
			if (Generator.integer(c)) code.variable(Opcodes.ILOAD, local);
			else if (c == long.class) code.variable(Opcodes.LLOAD, local);
			else if (c == float.class) code.variable(Opcodes.FLOAD, local);
			else if (c == double.class) code.variable(Opcodes.DLOAD, local);
			else code.variable(Opcodes.ALOAD, local);
			local += Generator.typeSize(c);
		}
		code.method(0xB6 + kind, Generator.getType(target), invocation.name(), invocation.type().toMethodDescriptorString(), kind == ReflectionFactory.KIND_INVOKE_INTERFACE);
		Class<?> c = implementation.type().returnType();
		if (c == void.class)
		{
			if (invocation.type().returnType() != void.class)
			{
				if (Generator.typeSize(invocation.type().returnType()) == 2) code.instruction(Opcodes.POP2);
				else code.instruction(Opcodes.POP);
			}
			code.instruction(Opcodes.RETURN);
		}
		else if (Generator.integer(c)) code.instruction(Opcodes.IRETURN);
		else if (c == long.class) code.instruction(Opcodes.LRETURN);
		else if (c == float.class) code.instruction(Opcodes.FRETURN);
		else if (c == double.class) code.instruction(Opcodes.DRETURN);
		else code.instruction(Opcodes.ARETURN);
		code.max(Math.max(Generator.typeSize(invocation.type().returnType()), local-1), local);
	}
}
