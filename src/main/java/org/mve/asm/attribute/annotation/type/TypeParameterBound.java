package org.mve.asm.attribute.annotation.type;

import org.mve.asm.file.attribute.annotation.type.TypeAnnotationTypeParameterBoundValue;
import org.mve.asm.file.constant.ConstantArray;

public class TypeParameterBound extends TypeAnnotationValue<TypeParameterBound>
{
	public int parameter;
	public int bound;

	public TypeParameterBound(int type, int parameter, int bound)
	{
		super(type);
		this.parameter = parameter;
		this.bound = bound;
	}

	public TypeParameterBound()
	{
		super(0);
	}

	public TypeParameterBound parameter(int parameter)
	{
		this.parameter = parameter;
		return this;
	}

	public TypeParameterBound bound(int bound)
	{
		this.bound = bound;
		return this;
	}

	@Override
	public org.mve.asm.file.attribute.annotation.type.TypeAnnotationValue value(ConstantArray array)
	{
		TypeAnnotationTypeParameterBoundValue value = new TypeAnnotationTypeParameterBoundValue();
		value.type = this.type;
		value.parameter = this.parameter;
		value.bound = this.bound;
		return value;
	}
}
