package org.mve.invoke.common.standard;

import org.mve.asm.ClassWriter;
import org.mve.asm.MethodWriter;
import org.mve.invoke.common.Generator;

import java.lang.reflect.Field;

public abstract class FieldModifierGenerator extends Generator
{
	private final Field field;

	public FieldModifierGenerator(Field field)
	{
		this.field = field;
	}

	public abstract void generate(MethodWriter method, ClassWriter classWriter);

	public Field getField()
	{
		return field;
	}
}
