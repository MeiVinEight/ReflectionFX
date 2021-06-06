package org.mve.asm.file;

public class VerificationTopVariable extends Verification
{
	@Override
	public VerificationType getType()
	{
		return VerificationType.ITEM_TOP;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{0};
	}
}
