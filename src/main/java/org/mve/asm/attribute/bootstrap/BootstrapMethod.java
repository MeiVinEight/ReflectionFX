package org.mve.asm.attribute.bootstrap;

import org.mve.asm.constant.MethodHandle;

import java.util.Arrays;

public class BootstrapMethod
{
	public MethodHandle method;
	public Object[] argument = new Object[0];

	public BootstrapMethod(MethodHandle method, Object... argument)
	{
		this.method = method;
		this.argument = argument;
	}

	public BootstrapMethod()
	{
	}

	public BootstrapMethod method(MethodHandle method)
	{
		this.method = method;
		return this;
	}

	public BootstrapMethod argument(Object value)
	{
		this.argument = Arrays.copyOf(this.argument, this.argument.length+1);
		this.argument[this.argument.length-1] = value;
		return this;
	}
}
