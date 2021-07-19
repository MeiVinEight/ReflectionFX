package org.mve.asm.attribute.annotation.type;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationCatchValue;
import org.mve.asm.file.constant.ConstantArray;

public class Catch extends TypeAnnotationValue<Catch>
{
	public Marker exception;

	public Catch(int type, Marker exception)
	{
		super(type);
		this.exception = exception;
	}

	public Catch()
	{
		super(0);
	}

	public Catch exception(Marker exception)
	{
		this.exception = exception;
		return this;
	}

	@Override
	public org.mve.asm.file.attribute.annotation.type.TypeAnnotationValue value(ConstantArray array)
	{
		TypeAnnotationCatchValue value = new TypeAnnotationCatchValue();
		value.type = this.type;
		value.exception = this.exception.address;
		return value;
	}
}
