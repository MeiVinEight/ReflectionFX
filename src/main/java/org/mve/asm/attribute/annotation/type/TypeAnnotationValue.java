package org.mve.asm.attribute.annotation.type;

import org.mve.asm.file.constant.ConstantArray;

public abstract class TypeAnnotationValue<T extends TypeAnnotationValue<T>>
{
	public int type;

	public TypeAnnotationValue(int type)
	{
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public T type(int type)
	{
		this.type = type;
		return (T) this;
	}

	public abstract org.mve.asm.file.attribute.annotation.type.TypeAnnotationValue value(ConstantArray array);
}
