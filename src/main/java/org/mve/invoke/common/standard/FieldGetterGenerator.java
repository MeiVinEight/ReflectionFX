package org.mve.invoke.common.standard;

import java.lang.reflect.Field;

public abstract class FieldGetterGenerator extends FieldModifierGenerator
{
	public FieldGetterGenerator(Field field)
	{
		super(field);
	}
}
