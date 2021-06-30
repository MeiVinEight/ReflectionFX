package org.mve.asm.attribute.code.stack;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.stack.StackMapFrameType;
import org.mve.asm.file.attribute.stack.StackMapFullFrame;
import org.mve.asm.attribute.code.stack.verification.Verification;

import java.util.Arrays;

public class FullFrame extends StackMapFrame
{
	public Verification[] local;
	public Verification[] stack;

	@Override
	public FullFrame mark(Marker marker)
	{
		return (FullFrame) super.mark(marker);
	}

	public FullFrame local(Verification verification)
	{
		this.local = Arrays.copyOf(this.local, this.local.length+1);
		this.local[this.local.length-1] = verification;
		return this;
	}

	public FullFrame stack(Verification verification)
	{
		this.stack = Arrays.copyOf(this.stack, this.stack.length+1);
		this.stack[this.stack.length-1] = verification;
		return this;
	}

	@Override
	public org.mve.asm.file.attribute.stack.StackMapFrame transform(int previous, ConstantArray pool)
	{
		StackMapFullFrame frame = new StackMapFullFrame();
		frame.type = StackMapFrameType.STACK_MAP_FULL_FRAME.high();
		frame.offset = this.marker.address - previous;
		for (Verification verification : this.local)
		{
			frame.local(verification.transform(pool));
		}

		for (Verification verification : this.stack)
		{
			frame.stack(verification.transform(pool));
		}
		return frame;
	}
}
