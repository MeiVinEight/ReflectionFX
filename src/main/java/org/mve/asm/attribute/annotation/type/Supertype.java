package org.mve.asm.attribute.annotation.type;

import org.mve.asm.file.attribute.annotation.type.TypeAnnotationSupertypeValue;
import org.mve.asm.file.constant.ConstantArray;

public class Supertype extends TypeAnnotationValue<Supertype>
{
	public int supertype;

	public Supertype(int type, int supertype)
	{
		super(type);
		this.supertype = supertype;
	}

	public Supertype()
	{
		super(0);
	}

	public Supertype supertype(int supertype)
	{
		this.supertype = supertype;
		return this;
	}

	@Override
	public org.mve.asm.file.attribute.annotation.type.TypeAnnotationValue value(ConstantArray array)
	{
		TypeAnnotationSupertypeValue value = new TypeAnnotationSupertypeValue();
		value.type = this.type;
		value.supertype = this.supertype;
		return value;
	}
}
