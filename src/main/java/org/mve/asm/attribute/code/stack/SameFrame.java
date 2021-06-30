package org.mve.asm.attribute.code.stack;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.stack.StackMapSameFrame;

public class SameFrame extends StackMapFrame
{
	@Override
	public SameFrame mark(Marker marker)
	{
		return (SameFrame) super.mark(marker);
	}

	@Override
	public org.mve.asm.file.attribute.stack.StackMapFrame transform(int previous, ConstantArray pool)
	{
		StackMapSameFrame frame = new StackMapSameFrame();
		frame.type = this.marker.address - previous;
		return frame;
	}
}
