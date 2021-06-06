package org.mve.asm.file;

public class VerificationLongVariable extends Verification
{
	@Override
	public VerificationType getType()
	{
		return VerificationType.ITEM_LONG;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{4};
	}
}
