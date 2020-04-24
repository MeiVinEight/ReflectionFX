package org.mve.util.asm;

public class Type
{
	public static String getName(Class<?> clazz)
	{
		StringBuilder type = new StringBuilder();
		while (clazz.isArray())
		{
			type.append('[');
			clazz = clazz.getComponentType();
		}
		if (clazz == void.class) type.append('V');
		else if (clazz == byte.class) type.append('B');
		else if (clazz == short.class) type.append('S');
		else if (clazz == int.class) type.append('I');
		else if (clazz == long.class) type.append('J');
		else if (clazz == float.class) type.append('F');
		else if (clazz == double.class) type.append('D');
		else if (clazz == boolean.class) type.append('Z');
		else if (clazz == char.class) type.append('C');
		else type.append(clazz.getTypeName().replace('.', '/'));
		return type.toString();
	}

	public static String getDescription(Class<?> type)
	{
		return type.isPrimitive() ? getName(type) : "L"+getName(type)+";";
	}
}
