package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.method.Argument;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeMethodArguments;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class MethodArgumentWriter implements AttributeWriter
{
	public Argument[] argument = new Argument[0];

	public MethodArgumentWriter argument(String name, int access)
	{
		return this.argument(new Argument(name, access));
	}

	public MethodArgumentWriter argument(Argument argument)
	{
		this.argument = Arrays.copyOf(this.argument, this.argument.length+1);
		this.argument[this.argument.length-1] = argument;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeMethodArguments attribute = new AttributeMethodArguments();
		attribute.name = ConstantPoolFinder.findUTF8(pool, attribute.type().getName());
		for (Argument argument : this.argument)
		{
			org.mve.asm.file.attribute.method.Argument a = new org.mve.asm.file.attribute.method.Argument();
			a.name = ConstantPoolFinder.findUTF8(pool, argument.name);
			a.access = argument.access;
			attribute.argument(a);
		}
		return attribute;
	}
}
