package org.mve.invoke;

import java.lang.reflect.Field;

public abstract class FieldGetterGenerator extends FieldModifierGenerator
{
	public FieldGetterGenerator(Field field)
	{
		super(field);
	}
}
