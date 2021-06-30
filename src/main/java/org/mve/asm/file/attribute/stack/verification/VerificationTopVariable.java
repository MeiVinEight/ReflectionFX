package org.mve.asm.file.attribute.stack.verification;

public class VerificationTopVariable extends Verification
{
	@Override
	public VerificationType type()
	{
		return VerificationType.ITEM_TOP;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{0};
	}
}
