package org.mve.asm.attribute.code.stack;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.stack.StackMapFrameType;
import org.mve.asm.file.attribute.stack.StackMapSameLocals1StackItemFrameExtended;
import org.mve.asm.attribute.code.stack.verification.Verification;

public class SameLocals1StackItemFrameExtended extends StackMapFrame
{
	public Verification verification;

	@Override
	public SameLocals1StackItemFrameExtended mark(Marker marker)
	{
		return (SameLocals1StackItemFrameExtended) super.mark(marker);
	}

	public SameLocals1StackItemFrameExtended verification(Verification verification)
	{
		this.verification = verification;
		return this;
	}

	@Override
	public org.mve.asm.file.attribute.stack.StackMapFrame transform(int previous, ConstantArray pool)
	{
		StackMapSameLocals1StackItemFrameExtended frame = new StackMapSameLocals1StackItemFrameExtended();
		frame.type = StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME_EXTENDED.low();
		frame.offset = this.marker.address - previous;
		frame.verification = this.verification.transform(pool);
		return frame;
	}
}
