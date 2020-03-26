package org.mve.util.asm;

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
}
