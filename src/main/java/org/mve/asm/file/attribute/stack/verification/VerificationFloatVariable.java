package org.mve.asm.file.attribute.stack.verification;

public class VerificationFloatVariable extends Verification
{
	@Override
	public VerificationType type()
	{
		return VerificationType.ITEM_FLOAT;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{2};
	}
}
