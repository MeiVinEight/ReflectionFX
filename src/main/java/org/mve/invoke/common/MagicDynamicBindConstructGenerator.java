package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;
import org.mve.invoke.MethodKind;

public class MagicDynamicBindConstructGenerator extends DynamicBindConstructGenerator
{
	public MagicDynamicBindConstructGenerator(Class<?> target, MethodKind implementation, MethodKind invocation)
	{
		super(target, implementation, invocation);
	}

	@Override
	public void generate(ClassWriter bytecode)
	{
		Class<?> target = getTarget();
		MethodKind implementation = this.implementation();
		MethodKind invocation = this.invocation();
		MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, implementation().name(), implementation().type().toMethodDescriptorString());
		bytecode.addMethod(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.addAttribute(code);
		code.type(Opcodes.NEW, Generator.getType(target))
			.instruction(Opcodes.DUP)
			.max(2 + Generator.parameterSize(implementation.type().parameterArray()), 1 + Generator.parameterSize(implementation.type().parameterArray()));
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
		code
			.method(Opcodes.INVOKESPECIAL, Generator.getType(target), "<init>", invocation.type().toMethodDescriptorString(), false)
			.instruction(Opcodes.ARETURN);
	}
}
