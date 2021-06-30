package org.mve.asm.attribute.code.stack;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.constant.ConstantArray;

public abstract class StackMapFrame
{
	public Marker marker;

	public StackMapFrame mark(Marker marker)
	{
		this.marker = marker;
		return this;
	}

	public int mark()
	{
		return this.marker.address;
	}

	public abstract org.mve.asm.file.attribute.stack.StackMapFrame transform(int previous, ConstantArray pool);
}
