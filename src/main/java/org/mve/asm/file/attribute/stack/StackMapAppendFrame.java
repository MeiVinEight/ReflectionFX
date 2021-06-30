package org.mve.asm.file.attribute.stack;

import org.mve.asm.file.attribute.stack.verification.Verification;

public class StackMapAppendFrame extends StackMapFrame
{
	public int offset;
	/**
	 * verification.length = frame_type - 251
	 */
	public Verification[] verification;

	@Override
	public StackMapFrameType type()
	{
		return StackMapFrameType.STACK_MAP_APPEND_FRAME;
	}

	@Override
	public int length()
	{
		int len = 3;
		for (Verification verification : verification) len += verification.type().length();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) this.type;
		b[index++] = (byte) ((this.offset >>> 8) & 0XFF);
		b[index++] = (byte) (this.offset & 0XFF);
		for (Verification v : this.verification)
		{
			int l = v.type().length();
			System.arraycopy(v.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
