package org.mve.util.asm.file;

public class StackMapSameLocals1StackItemFrameExtended extends StackMapFrame
{
	private short offsetDelta;
	private Verification verification;

	public StackMapSameLocals1StackItemFrameExtended(byte frameType)
	{
		super(frameType);
	}

	public short getOffsetDelta()
	{
		return offsetDelta;
	}

	public void setOffsetDelta(short offsetDelta)
	{
		this.offsetDelta = offsetDelta;
	}

	public Verification getVerification()
	{
		return verification;
	}

	public void setVerification(Verification verification)
	{
		this.verification = verification;
	}

	@Override
	public StackMapFrameType getType()
	{
		return StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME_EXTENDED;
	}

	@Override
	public int getLength()
	{
		return 3 + this.verification.getType().getLength();
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[this.getLength()];
		b[0] = this.getFrameType();
		b[1] = (byte) ((this.offsetDelta >>> 8) & 0XFF);
		b[2] = (byte) (this.offsetDelta & 0XFF);
		System.arraycopy(this.verification.toByteArray(), 0, b, 3, this.verification.getType().getLength());
		return b;
	}
}
