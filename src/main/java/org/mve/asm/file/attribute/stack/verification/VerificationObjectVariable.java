package org.mve.asm.file.attribute.stack.verification;

public class VerificationObjectVariable extends Verification
{
	public int type;

	@Override
	public VerificationType type()
	{
		return VerificationType.ITEM_OBJECT;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{7, (byte) ((this.type >>> 8) & 0XFF), (byte) (this.type & 0XFF)};
	}
}
