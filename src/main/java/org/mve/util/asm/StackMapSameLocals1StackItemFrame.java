package org.mve.util.asm;

public class StackMapSameLocals1StackItemFrame extends StackMapFrame
{
	private Verification verification;

	public StackMapSameLocals1StackItemFrame(byte frameType)
	{
		super(frameType);
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
		return StackMapFrameType.STACK_MAP_SAME_LOCALS_1_ITEM_FRAME;
	}

	@Override
	public int getLength()
	{
		return 1 + verification.getType().getLength();
	}
}
