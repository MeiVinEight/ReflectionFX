package org.mve.asm.file.attribute.stack.verification;

public class VerificationNullVariable extends Verification
{
	@Override
	public VerificationType type()
	{
		return VerificationType.ITEM_NULL;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{5};
	}
}
