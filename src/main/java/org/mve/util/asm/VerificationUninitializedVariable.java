package org.mve.util.asm;

public class VerificationUninitializedVariable extends Verification
{
	private short offset;

	public short getOffset()
	{
		return offset;
	}

	public void setOffset(short offset)
	{
		this.offset = offset;
	}

	@Override
	public VerificationType getType()
	{
		return VerificationType.ITEM_UNINITIALIZED;
	}
}
