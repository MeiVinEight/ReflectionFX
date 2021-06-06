package org.mve.asm.stack;

import org.mve.asm.Marker;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.StackMapFrameType;
import org.mve.asm.file.StackMapSameFrameExtended;

public class SameFrameExtended extends StackMapFrame
{
	public SameFrameExtended(Marker marker)
	{
		super(marker);
	}

	@Override
	public org.mve.asm.file.StackMapFrame transform(int previous, ConstantPool pool)
	{
		StackMapSameFrameExtended frame = new StackMapSameFrameExtended(StackMapFrameType.STACK_MAP_SAME_FRAME_EXTENDED.getHigh());
		frame.setOffsetDelta((short) (this.marker.get() - previous));
		return frame;
	}
}
