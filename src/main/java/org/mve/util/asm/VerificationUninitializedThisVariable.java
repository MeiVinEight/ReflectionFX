package org.mve.util.asm;

public class VerificationUninitializedThisVariable extends Verification
{
	@Override
	public VerificationType getType()
	{
		return VerificationType.ITEM_UNINITIALIZED_THIS;
	}
}
