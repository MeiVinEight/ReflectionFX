package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeExceptions;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class ExceptionWriter implements AttributeWriter
{
	public String[] exception = new String[0];

	public ExceptionWriter(String... exception)
	{
		this.exception = exception;
	}

	public ExceptionWriter()
	{
	}

	public ExceptionWriter exception(String exception)
	{
		this.exception = Arrays.copyOf(this.exception, this.exception.length+1);
		this.exception[this.exception.length-1] = exception;
		return this;
	}

	public ExceptionWriter mark(Marker marker)
	{
		marker.address = this.exception.length;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeExceptions attribute = new AttributeExceptions();
		attribute.name = ConstantPoolFinder.findUTF8(pool, AttributeType.EXCEPTIONS.getName());
		for (String exception : this.exception)
		{
			attribute.exception(ConstantPoolFinder.findClass(pool, exception));
		}
		return attribute;
	}
}
