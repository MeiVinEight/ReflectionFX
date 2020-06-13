package org.mve.util.asm.file;

public class VerificationFloatVariable extends Verification
{
	@Override
	public VerificationType getType()
	{
		return VerificationType.ITEM_FLOAT;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{2};
	}
}
