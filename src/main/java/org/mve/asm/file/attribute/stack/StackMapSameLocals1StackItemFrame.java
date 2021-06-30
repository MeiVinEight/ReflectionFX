package org.mve.asm.file.attribute.stack;

import org.mve.asm.file.attribute.stack.verification.Verification;

public class StackMapSameLocals1StackItemFrame extends StackMapFrame
{
	public Verification verification;

	@Override
	public StackMapFrameType type()
	{
		return StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME;
	}

	@Override
	public int length()
	{
		return 1 + verification.type().length();
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[this.length()];
		b[0] = (byte) this.type;
		System.arraycopy(this.verification.toByteArray(), 0, b, 1, this.verification.type().length());
		return b;
	}
}
