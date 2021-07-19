package org.mve.asm.attribute.annotation.type;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationThrowsValue;
import org.mve.asm.file.constant.ConstantArray;

public class Throws extends TypeAnnotationValue<Throws>
{
	public Marker exception;

	public Throws(int type, Marker exception)
	{
		super(type);
		this.exception = exception;
	}

	public Throws()
	{
		super(0);
	}

	public Throws exception(Marker exception)
	{
		this.exception = exception;
		return this;
	}

	@Override
	public org.mve.asm.file.attribute.annotation.type.TypeAnnotationValue value(ConstantArray array)
	{
		TypeAnnotationThrowsValue value = new TypeAnnotationThrowsValue();
		value.type = this.type;
		value.thrown = this.exception.address;
		return null;
	}
}
