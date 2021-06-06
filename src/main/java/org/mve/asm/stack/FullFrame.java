package org.mve.asm.stack;

import org.mve.asm.Marker;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.StackMapFrameType;
import org.mve.asm.file.StackMapFullFrame;
import org.mve.asm.stack.verification.Verification;

public class FullFrame extends StackMapFrame
{
	private final Verification[] local;
	private final Verification[] stack;

	public FullFrame(Marker marker, Verification[] local, Verification[] stack)
	{
		super(marker);
		this.local = local;
		this.stack = stack;
	}

	@Override
	public org.mve.asm.file.StackMapFrame transform(int previous, ConstantPool pool)
	{
		StackMapFullFrame frame = new StackMapFullFrame(StackMapFrameType.STACK_MAP_FULL_FRAME.getHigh());
		frame.setOffsetDelta((short) (this.marker.get() - previous));
		for (Verification verification : this.local)
		{
			frame.addLocal(verification.transform(pool));
		}

		for (Verification verification : this.stack)
		{
			frame.addStackItem(verification.transform(pool));
		}
		return frame;
	}
}
