package org.mve.asm.stack;

import org.mve.asm.Marker;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.StackMapFrameType;
import org.mve.asm.file.StackMapSameLocals1StackItemFrameExtended;
import org.mve.asm.stack.verification.Verification;

public class SameLocals1StackItemFrameExtended extends StackMapFrame
{
	private final Verification verification;

	public SameLocals1StackItemFrameExtended(Marker offset, Verification verification)
	{
		super(offset);
		this.verification = verification;
	}

	@Override
	public org.mve.asm.file.StackMapFrame transform(int previous, ConstantPool pool)
	{
		StackMapSameLocals1StackItemFrameExtended frame = new StackMapSameLocals1StackItemFrameExtended(StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME_EXTENDED.getLow());
		frame.setOffsetDelta((short) (this.marker.get() - previous));
		frame.setVerification(this.verification.transform(pool));
		return frame;
	}
}
