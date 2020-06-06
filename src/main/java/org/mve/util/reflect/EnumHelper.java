package org.mve.util.reflect;

public interface EnumHelper<T>
{
	T construct(String name);

	T construct(String name, int ordinal);

	T[] values();

	void values(T[] values);

	void add(T value);

	void remove(int index);
}
