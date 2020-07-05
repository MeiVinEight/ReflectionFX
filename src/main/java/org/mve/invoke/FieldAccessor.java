package org.mve.invoke;

import java.lang.reflect.Field;

public interface FieldAccessor<T> extends ReflectionAccessor<T>
{
	Field getField();
}
