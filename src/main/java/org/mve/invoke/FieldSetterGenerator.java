package org.mve.invoke;

import java.lang.reflect.Field;

public abstract class FieldSetterGenerator extends FieldModifierGenerator
{
	public FieldSetterGenerator(Field field)
	{
		super(field);
	}
}
