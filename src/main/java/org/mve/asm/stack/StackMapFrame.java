package org.mve.asm.stack;

import org.mve.asm.Marker;
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
		return this.marker.get();
	}

	public abstract org.mve.asm.file.StackMapFrame transform(int previous, ConstantPool pool);
}
