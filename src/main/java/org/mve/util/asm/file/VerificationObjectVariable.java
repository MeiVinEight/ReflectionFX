package org.mve.util.asm.file;

public class VerificationObjectVariable extends Verification
{
	private short constantPoolIndex;

	public short getConstantPoolIndex()
	{
		return constantPoolIndex;
	}

	public void setConstantPoolIndex(short constantPoolIndex)
	{
		this.constantPoolIndex = constantPoolIndex;
	}

	@Override
	public VerificationType getType()
	{
		return VerificationType.ITEM_OBJECT;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{7, (byte) ((this.constantPoolIndex >>> 8) & 0XFF), (byte) (this.constantPoolIndex & 0XFF)};
	}
}
