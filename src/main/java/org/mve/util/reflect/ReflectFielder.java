package org.mve.util.reflect;

public interface ReflectFielder
{
	Object get(Object owner);

	void set(Object owner, Object obj);
}
