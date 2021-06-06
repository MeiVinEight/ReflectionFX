package org.mve.invoke;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.asm.file.AccessFlag;

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
		MethodWriter mw = new MethodWriter().set(AccessFlag.ACC_PUBLIC, implementation.name(), implementation.type().toMethodDescriptorString());
		bytecode.addMethod(mw);
		Generator.inline(mw);
		CodeWriter code = new CodeWriter();
		mw.addAttribute(code);
		int local = 1;
		Class<?>[] params = implementation.type().parameterArray();
		for (Class<?> c : params)
		{
			if (Generator.integer(c)) code.addLocalVariableInstruction(Opcodes.ILOAD, local);
			else if (c == long.class) code.addLocalVariableInstruction(Opcodes.LLOAD, local);
			else if (c == float.class) code.addLocalVariableInstruction(Opcodes.FLOAD, local);
			else if (c == double.class) code.addLocalVariableInstruction(Opcodes.DLOAD, local);
			else code.addLocalVariableInstruction(Opcodes.ALOAD, local);
			local += Generator.typeSize(c);
		}
		code.addMethodInstruction(0xB6 + kind, Generator.getType(target), invocation.name(), invocation.type().toMethodDescriptorString(), kind == ReflectionFactory.KIND_INVOKE_INTERFACE);
		Class<?> c = implementation.type().returnType();
		if (c == void.class)
		{
			if (invocation.type().returnType() != void.class)
			{
				if (Generator.typeSize(invocation.type().returnType()) == 2) code.addInstruction(Opcodes.POP2);
				else code.addInstruction(Opcodes.POP);
			}
			code.addInstruction(Opcodes.RETURN);
		}
		else if (Generator.integer(c)) code.addInstruction(Opcodes.IRETURN);
		else if (c == long.class) code.addInstruction(Opcodes.LRETURN);
		else if (c == float.class) code.addInstruction(Opcodes.FRETURN);
		else if (c == double.class) code.addInstruction(Opcodes.DRETURN);
		else code.addInstruction(Opcodes.ARETURN);
		code.setMaxs(Math.max(Generator.typeSize(invocation.type().returnType()), local-1), local);
	}
}
