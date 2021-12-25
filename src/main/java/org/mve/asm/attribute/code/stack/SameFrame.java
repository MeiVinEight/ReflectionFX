package org.mve.asm.attribute.code.stack;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.attribute.stack.StackMapFrameType;
import org.mve.asm.file.attribute.stack.StackMapSameFrameExtended;
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
		int offset = this.marker.address - previous;
		if (offset > 63)
		{
			StackMapSameFrameExtended frame = new StackMapSameFrameExtended();
			frame.type = StackMapFrameType.STACK_MAP_SAME_FRAME_EXTENDED.low();
			frame.offset = offset;
			return frame;
		}
		else
		{
			StackMapSameFrame frame = new StackMapSameFrame();
			frame.type = this.marker.address - previous;
			return frame;
		}
	}
}
