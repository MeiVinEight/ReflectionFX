package org.mve.invoke;

import java.lang.invoke.MethodType;

public class StackFrame
{
	public final Class<?> clazz;
	public final String name;
	public final MethodType type;
	public final int offset;
	public final int line;
	public final String file;
	public final boolean natives;

	public StackFrame(Class<?> clazz, String name, MethodType type, int offset, int line, String file, boolean natives)
	{
		this.clazz = clazz;
		this.name = name;
		this.type = type;
		this.offset = offset;
		this.line = line;
		this.file = file;
		this.natives = natives;
	}
}
