package org.mve.asm.file.attribute.stack.verification;

public class VerificationUninitializedThisVariable extends Verification
{
	@Override
	public VerificationType type()
	{
		return VerificationType.ITEM_UNINITIALIZED_THIS;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{6};
	}
}
