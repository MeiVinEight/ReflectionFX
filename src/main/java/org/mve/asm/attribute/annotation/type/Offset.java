package org.mve.asm.attribute.annotation.type;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.attribute.annotation.type.TypeAnnotationOffsetValue;
import org.mve.asm.file.constant.ConstantArray;

public class Offset extends TypeAnnotationValue<Offset>
{
	public Marker offset;

	public Offset(int type, Marker offset)
	{
		super(type);
		this.offset = offset;
	}

	public Offset()
	{
		super(0);
	}

	public Offset offset(Marker offset)
	{
		this.offset = offset;
		return this;
	}

	@Override
	public org.mve.asm.file.attribute.annotation.type.TypeAnnotationValue value(ConstantArray array)
	{
		TypeAnnotationOffsetValue value = new TypeAnnotationOffsetValue();
		value.type = this.type;
		value.offset = this.offset.address;
		return value;
	}
}
