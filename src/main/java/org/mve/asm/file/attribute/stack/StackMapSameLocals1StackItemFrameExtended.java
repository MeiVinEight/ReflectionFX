package org.mve.asm.file.attribute.stack;

import org.mve.asm.file.attribute.stack.verification.Verification;

public class StackMapSameLocals1StackItemFrameExtended extends StackMapFrame
{
	public int offset;
	public Verification verification;

	@Override
	public StackMapFrameType type()
	{
		return StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME_EXTENDED;
	}

	@Override
	public int length()
	{
		return 3 + this.verification.type().length();
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[this.length()];
		b[0] = (byte) this.type;
		b[1] = (byte) ((this.offset >>> 8) & 0XFF);
		b[2] = (byte) (this.offset & 0XFF);
		System.arraycopy(this.verification.toByteArray(), 0, b, 3, this.verification.type().length());
		return b;
	}
}
