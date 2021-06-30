package org.mve.asm.file.attribute.stack.verification;

public class VerificationDoubleVariable extends Verification
{
	@Override
	public VerificationType type()
	{
		return VerificationType.ITEM_DOUBLE;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{3};
	}
}
