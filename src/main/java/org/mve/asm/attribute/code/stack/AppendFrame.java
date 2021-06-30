package org.mve.asm.attribute.code.stack;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.stack.StackMapAppendFrame;
import org.mve.asm.file.attribute.stack.StackMapFrameType;
import org.mve.asm.attribute.code.stack.verification.Verification;

import java.util.Arrays;

public class AppendFrame extends StackMapFrame
{
	/**
	 * length = k
	 * k = frame_type - 251
	 */
	public Verification[] verification;

	public AppendFrame verification(Verification verification)
	{
		this.verification = Arrays.copyOf(this.verification, this.verification.length+1);
		this.verification[this.verification.length-1] = verification;
		return this;
	}

	@Override
	public AppendFrame mark(Marker marker)
	{
		return (AppendFrame) super.mark(marker);
	}

	@Override
	public org.mve.asm.file.attribute.stack.StackMapFrame transform(int previous, ConstantArray pool)
	{
		StackMapAppendFrame frame = new StackMapAppendFrame();
		frame.type = StackMapFrameType.STACK_MAP_APPEND_FRAME.low() + this.verification.length - 1;
		frame.verification = new org.mve.asm.file.attribute.stack.verification.Verification[this.verification.length];
		frame.offset = this.marker.address - previous;
		for (int i = 0; i < this.verification.length; i++)
		{
			frame.verification[i] = this.verification[i].transform(pool);
		}
		return frame;
	}
}
