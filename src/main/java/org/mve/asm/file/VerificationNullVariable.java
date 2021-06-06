package org.mve.asm.file;

public class VerificationNullVariable extends Verification
{
	@Override
	public VerificationType getType()
	{
		return VerificationType.ITEM_NULL;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{5};
	}
}
