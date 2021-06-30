package org.mve.asm.file.attribute.stack.verification;

public class VerificationLongVariable extends Verification
{
	@Override
	public VerificationType type()
	{
		return VerificationType.ITEM_LONG;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{4};
	}
}
