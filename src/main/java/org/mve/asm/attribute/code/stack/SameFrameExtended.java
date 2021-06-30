package org.mve.asm.attribute.code.stack;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.stack.StackMapFrameType;
import org.mve.asm.file.attribute.stack.StackMapSameFrameExtended;

public class SameFrameExtended extends StackMapFrame
{
	@Override
	public SameFrameExtended mark(Marker marker)
	{
		return (SameFrameExtended) super.mark(marker);
	}

	@Override
	public org.mve.asm.file.attribute.stack.StackMapFrame transform(int previous, ConstantArray pool)
	{
		StackMapSameFrameExtended frame = new StackMapSameFrameExtended();
		frame.type = StackMapFrameType.STACK_MAP_SAME_FRAME_EXTENDED.high();
		frame.offset = this.marker.address - previous;
		return frame;
	}
}
