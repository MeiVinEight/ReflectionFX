package org.mve.asm.attribute.code.stack;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.stack.StackMapFrameType;
import org.mve.asm.file.attribute.stack.StackMapSameLocals1StackItemFrame;
import org.mve.asm.attribute.code.stack.verification.Verification;

/**
 * offset = frame_type - 64
 */
public class SameLocals1StackItemFrame extends StackMapFrame
{
	public Verification verification;

	@Override
	public SameLocals1StackItemFrame mark(Marker marker)
	{
		return (SameLocals1StackItemFrame) super.mark(marker);
	}

	public SameLocals1StackItemFrame verification(Verification verification)
	{
		this.verification = verification;
		return this;
	}

	@Override
	public org.mve.asm.file.attribute.stack.StackMapFrame transform(int previous, ConstantArray pool)
	{
		StackMapSameLocals1StackItemFrame frame = new StackMapSameLocals1StackItemFrame();
		frame.type = StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME.low() + (this.marker.address - previous);
		frame.verification = this.verification.transform(pool);
		return frame;
	}
}
