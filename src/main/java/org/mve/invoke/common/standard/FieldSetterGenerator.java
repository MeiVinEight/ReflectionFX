package org.mve.invoke.common.standard;

import java.lang.reflect.Field;

public abstract class FieldSetterGenerator extends FieldModifierGenerator
{
	public FieldSetterGenerator(Field field)
	{
		super(field);
	}
}
