package org.mve.util.reflect;

public interface StackAccessor
{
	Class<?> getCallerClass();

	Class<?>[] getStackClassContext();
}
