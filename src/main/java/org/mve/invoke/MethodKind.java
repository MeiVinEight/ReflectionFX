package org.mve.invoke;

import java.lang.invoke.MethodType;

public class MethodKind
{
	private final String name;
	private final MethodType type;

	public MethodKind(String name, MethodType type)
	{
		this.name = name;
		this.type = type;
	}

	public MethodKind(String name, Class<?> returnType, Class<?>... params)
	{
		this.name = name;
		this.type = MethodType.methodType(returnType, params);
	}

	public String name()
	{
		return this.name;
	}

	public MethodType type()
	{
		return this.type;
	}

	public static MethodKind match(MethodKind[] pattern, Class<?> clazz)
	{
		for (MethodKind kind : pattern)
		{
			try
			{
				clazz.getDeclaredMethod(kind.name(), kind.type().parameterArray());
				return kind;
			}
			catch (NoSuchMethodException ignored)
			{
			}
		}
		return new MethodKind("", void.class);
	}
}
