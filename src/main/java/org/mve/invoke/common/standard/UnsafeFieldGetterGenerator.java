package org.mve.invoke.common.standard;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ReflectionAccessor;
import org.mve.invoke.Unsafe;
import org.mve.invoke.common.Generator;
import org.mve.invoke.common.JavaVM;

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
		long off = statics ? Unsafe.unsafe.staticFieldOffset(field) : Unsafe.unsafe.objectFieldOffset(field);
		CodeWriter code = new CodeWriter();
		method.attribute(code);
		code.field(Opcodes.GETSTATIC, Generator.type(Unsafe.class), "unsafe", Generator.signature(Unsafe.class));
		if (statics)
		{
			code.field(Opcodes.GETSTATIC, classWriter.name, JavaVM.CONSTANT[ReflectionAccessor.FIELD_CLASS], Generator.signature(Class.class));
		}
		else
		{
			code.instruction(Opcodes.ALOAD_1);
		}
		code.constant(off);
		Generator.unsafeget(field.getType(), code);
		Generator.returner(field.getType(), code);
		code.max(4, statics ? 1 : 2);
	}
}
