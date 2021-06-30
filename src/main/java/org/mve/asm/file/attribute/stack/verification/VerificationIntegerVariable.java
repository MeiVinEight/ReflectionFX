package org.mve.asm.file.attribute.stack.verification;

public class VerificationIntegerVariable extends Verification
{
	@Override
	public VerificationType type()
	{
		return VerificationType.ITEM_INTEGER;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{1};
	}
}
