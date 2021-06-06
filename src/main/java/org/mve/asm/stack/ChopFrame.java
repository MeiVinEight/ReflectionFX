package org.mve.asm.stack;

import org.mve.asm.Marker;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.StackMapChopFrame;
import org.mve.asm.file.StackMapFrameType;

public class ChopFrame extends StackMapFrame
{
	/**
	 * k, k = 251 - frame_type
	 */
	private final int chop;

	public ChopFrame(Marker marker, int chop)
	{
		super(marker);
		this.chop = chop;
	}

	@Override
	public org.mve.asm.file.StackMapFrame transform(int previous, ConstantPool pool)
	{
		StackMapChopFrame frame = new StackMapChopFrame((byte) ((StackMapFrameType.STACK_MAP_CHOP_FRAME.getHigh() - this.chop) + 1));
		frame.setOffsetDelta((short) (this.marker.get() - previous));
		return frame;
	}
}
