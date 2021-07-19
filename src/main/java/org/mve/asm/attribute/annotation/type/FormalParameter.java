package org.mve.asm.attribute.annotation.type;

import org.mve.asm.file.attribute.annotation.type.TypeAnnotationFormalParameterValue;
import org.mve.asm.file.constant.ConstantArray;

public class FormalParameter extends TypeAnnotationValue<FormalParameter>
{
	public int parameter;

	public FormalParameter(int type, int parameter)
	{
		super(type);
		this.parameter = parameter;
	}

	public FormalParameter()
	{
		super(0);
	}

	public FormalParameter parameter(int parameter)
	{
		this.parameter = parameter;
		return this;
	}

	@Override
	public org.mve.asm.file.attribute.annotation.type.TypeAnnotationValue value(ConstantArray array)
	{
		TypeAnnotationFormalParameterValue value = new TypeAnnotationFormalParameterValue();
		value.type = this.type;
		value.parameter = this.parameter;
		return value;
	}
}
