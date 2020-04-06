package org.mve.util.reflect;

import java.lang.reflect.AccessibleObject;

public interface Accessor
{
	void setAccessible(AccessibleObject acc, boolean flag);
}
