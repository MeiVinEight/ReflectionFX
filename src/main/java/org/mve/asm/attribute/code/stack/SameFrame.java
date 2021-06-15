package org.mve.asm.attribute.code.stack;

import org.mve.asm.attribute.code.Marker;
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
		return new StackMapSameFrame((byte) (this.marker.address - previous));
	}
}
