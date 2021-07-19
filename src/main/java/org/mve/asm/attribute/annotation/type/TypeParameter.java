package org.mve.asm.attribute.annotation.type;

import org.mve.asm.file.attribute.annotation.type.TypeAnnotationTypeParameterValue;
import org.mve.asm.file.constant.ConstantArray;

public class TypeParameter extends TypeAnnotationValue<TypeParameter>
{
	public int parameter;

	public TypeParameter(int type, int parameter)
	{
		super(type);
		this.parameter = parameter;
	}

	public TypeParameter()
	{
		super(0);
	}

	public TypeParameter parameter(int parameter)
	{
		this.parameter = parameter;
		return this;
	}

	@Override
	public org.mve.asm.file.attribute.annotation.type.TypeAnnotationValue value(ConstantArray array)
	{
		TypeAnnotationTypeParameterValue value = new TypeAnnotationTypeParameterValue();
		value.type = this.type;
		value.parameter = this.parameter;
		return value;
	}
}
