package org.mve.asm.attribute.annotation.type;

import org.mve.asm.file.attribute.annotation.type.TypeAnnotationEmptyValue;
import org.mve.asm.file.constant.ConstantArray;

public class Empty extends TypeAnnotationValue<Empty>
{
	public Empty(int type)
	{
		super(type);
	}

	public Empty()
	{
		super(0);
	}

	@Override
	public org.mve.asm.file.attribute.annotation.type.TypeAnnotationValue value(ConstantArray array)
	{
		TypeAnnotationEmptyValue value = new TypeAnnotationEmptyValue();
		value.type = this.type;
		return value;
	}
}
