package org.mve.util.asm;

public class Type
{
	private final String type;

	public Type(String type)
	{
		this.type = type;
	}

	public Type(Class<?> c)
	{
		this(getName(c));
	}

	public String getType()
	{
		return type;
	}

	public static String getName(Class<?> clazz)
	{
		StringBuilder type = new StringBuilder();
		boolean array = clazz.isArray();
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
		else
		{
			if (array) type.append('L');
			type.append(clazz.getTypeName().replace('.', '/'));
			if (array) type.append(';');
		}
		return type.toString();
	}

	public static String getDescription(Class<?> type)
	{
		return type.isPrimitive() ? getName(type) : "L"+getName(type)+";";
	}

	public static int getArgumentsAndReturnSizes(String methodDescriptor) {
		int argumentsSize = 1;
		int currentOffset = 1;

		char currentChar;
		for(currentChar = methodDescriptor.charAt(currentOffset); currentChar != ')'; currentChar = methodDescriptor.charAt(currentOffset))
		{
			if (currentChar != 'J' && currentChar != 'D')
			{
				while(methodDescriptor.charAt(currentOffset) == '[') ++currentOffset;

				if (methodDescriptor.charAt(currentOffset++) == 'L') currentOffset = methodDescriptor.indexOf(59, currentOffset) + 1;

				++argumentsSize;
			}
			else
			{
				++currentOffset;
				argumentsSize += 2;
			}
		}

		currentChar = methodDescriptor.charAt(currentOffset + 1);
		if (currentChar == 'V') return argumentsSize << 2;
		else
		{
			int returnSize = currentChar != 'J' && currentChar != 'D' ? 1 : 2;
			return argumentsSize << 2 | returnSize;
		}
	}
}
