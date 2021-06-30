package org.mve.asm.attribute.code.stack;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.stack.StackMapChopFrame;
import org.mve.asm.file.attribute.stack.StackMapFrameType;

public class ChopFrame extends StackMapFrame
{
	/**
	 * k, k = 251 - frame_type
	 */
	public int chop;

	/**
	 * k, frame_type = 251 - k
	 *
	 * @param i count of local variables to chop
	 * @return this
	 */
	public ChopFrame chop(int i)
	{
		this.chop = 251 - i;
		return this;
	}

	@Override
	public ChopFrame mark(Marker marker)
	{
		return (ChopFrame) super.mark(marker);
	}

	@Override
	public org.mve.asm.file.attribute.stack.StackMapFrame transform(int previous, ConstantArray pool)
	{
		StackMapChopFrame frame = new StackMapChopFrame();
		frame.type = (StackMapFrameType.STACK_MAP_CHOP_FRAME.high() - this.chop) + 1;
		frame.offset =this.marker.address - previous;
		return frame;
	}
}
