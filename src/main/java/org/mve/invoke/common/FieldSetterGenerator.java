package org.mve.invoke.common;

import java.lang.reflect.Field;

public abstract class FieldSetterGenerator extends FieldModifierGenerator
{
	public FieldSetterGenerator(Field field)
	{
		super(field);
	}
}
