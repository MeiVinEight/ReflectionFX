package org.mve.util.asm.file;

public class VerificationIntegerVariable extends Verification
{
	@Override
	public VerificationType getType()
	{
		return VerificationType.ITEM_INTEGER;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{1};
	}
}
