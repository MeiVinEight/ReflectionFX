package org.mve.asm.stack;

import org.mve.asm.Marker;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.StackMapSameFrame;

public class SameFrame extends StackMapFrame
{
	public SameFrame(Marker marker)
	{
		super(marker);
	}

	@Override
	public org.mve.asm.file.StackMapFrame transform(int previous, ConstantPool pool)
	{
		return new StackMapSameFrame((byte) (this.marker.get() - previous));
	}
}
