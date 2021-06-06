package org.mve.asm.stack;

import org.mve.asm.Marker;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.StackMapFrameType;
import org.mve.asm.file.StackMapSameLocals1StackItemFrame;
import org.mve.asm.stack.verification.Verification;

/**
 * offset = frame_type - 64
 */
public class SameLocals1StackItemFrame extends StackMapFrame
{
	private final Verification verification;

	public SameLocals1StackItemFrame(Marker marker, Verification verification)
	{
		super(marker);
		this.verification = verification;
	}

	@Override
	public org.mve.asm.file.StackMapFrame transform(int previous, ConstantPool pool)
	{
		StackMapSameLocals1StackItemFrame frame = new StackMapSameLocals1StackItemFrame((byte) (StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME.getLow() + (this.marker.get() - previous)));
		frame.setVerification(this.verification.transform(pool));
		return frame;
	}
}
