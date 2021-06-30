package org.mve.asm.file.attribute.stack.verification;

public class VerificationUninitializedVariable extends Verification
{
	public int offset;

	@Override
	public VerificationType type()
	{
		return VerificationType.ITEM_UNINITIALIZED;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{8, (byte) ((this.offset >>> 8) & 0XFF), (byte) (this.offset & 0XFF)};
	}
}
