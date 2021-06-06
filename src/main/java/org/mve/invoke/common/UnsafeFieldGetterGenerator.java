package org.mve.invoke.common;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class UnsafeFieldGetterGenerator extends FieldGetterGenerator
{
	public UnsafeFieldGetterGenerator(Field field)
	{
		super(field);
	}

	@Override
	public void generate(MethodWriter method, ClassWriter classWriter)
	{
		Field field = this.getField();
		int modifiers = field.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		long off = statics ? ReflectionFactory.UNSAFE.staticFieldOffset(field) : ReflectionFactory.UNSAFE.objectFieldOffset(field);
		CodeWriter code = new CodeWriter();
		method.addAttribute(code);
		code.addFieldInstruction(Opcodes.GETSTATIC, Generator.getType(ReflectionFactory.class), "UNSAFE", Generator.getSignature(Unsafe.class));
		if (statics)
		{
			code.addFieldInstruction(Opcodes.GETSTATIC, classWriter.getName(), "0", Generator.getSignature(Class.class));
		}
		else
		{
			code.addInstruction(Opcodes.ALOAD_1);
		}
		code.addConstantInstruction(off);
		Generator.unsafeget(field.getType(), code);
		Generator.returner(field.getType(), code);
		code.setMaxs(4, statics ? 1 : 2);
	}
}
