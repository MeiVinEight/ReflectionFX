package org.mve.invoke.common.standard;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.asm.Opcodes;
import org.mve.asm.attribute.CodeWriter;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.common.Generator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MagicFieldGetterGenerator extends FieldGetterGenerator
{
	public MagicFieldGetterGenerator(Field field)
	{
		super(field);
	}

	@Override
	public void generate(MethodWriter method, ClassWriter classWriter)
	{
		Field field = this.getField();
		int modifiers = field.getModifiers();
		boolean statics = Modifier.isStatic(modifiers);
		CodeWriter code = new CodeWriter();
		method.attribute(code);
		if (statics)
		{
			code.field(Opcodes.GETSTATIC, Generator.type(field.getDeclaringClass()), ReflectionFactory.ACCESSOR.getName(field), Generator.signature(field.getType()))
				.max(Generator.typeSize(field.getType()), 1);
		}
		else
		{
			code.instruction(Opcodes.ALOAD_1)
				.field(Opcodes.GETFIELD, Generator.type(field.getDeclaringClass()), ReflectionFactory.ACCESSOR.getName(field), Generator.signature(field.getType()))
				.max(Generator.typeSize(field.getType()), 2);
		}
		Generator.returner(field.getType(), code);
	}
}
