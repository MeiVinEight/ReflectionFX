package org.mve.asm.attribute.code.stack;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.ConstantPool;

public abstract class StackMapFrame
{
	protected final Marker marker;

	public StackMapFrame(Marker marker)
	{
		this.marker = marker;
	}

	public int mark()
	{
		return this.marker.address;
	}

	public abstract org.mve.asm.file.StackMapFrame transform(int previous, ConstantPool pool);
}
