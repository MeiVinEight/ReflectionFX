package org.mve.asm.attribute.annotation.type;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationTypeArgumentValue;
import org.mve.asm.file.constant.ConstantArray;

public class TypeArgument extends TypeAnnotationValue<TypeArgument>
{
	public Marker offset;
	public int argument;

	public TypeArgument(int type, Marker offset, int argument)
	{
		super(type);
		this.offset = offset;
		this.argument = argument;
	}

	public TypeArgument()
	{
		super(0);
	}

	public TypeArgument offset(Marker offset)
	{
		this.offset = offset;
		return this;
	}

	public TypeArgument argument(int argument)
	{
		this.argument = argument;
		return this;
	}

	@Override
	public org.mve.asm.file.attribute.annotation.type.TypeAnnotationValue value(ConstantArray array)
	{
		TypeAnnotationTypeArgumentValue value = new TypeAnnotationTypeArgumentValue();
		value.type = this.type;
		value.offset = this.offset.address;
		value.argument = this.argument;
		return value;
	}
}
