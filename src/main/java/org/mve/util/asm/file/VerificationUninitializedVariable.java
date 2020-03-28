package org.mve.util.asm.file;

public class VerificationUninitializedVariable extends Verification
{
	private short offset;

	public short getOffset()
	{
		return offset;
	}

	public void setOffset(short offset)
	{
		this.offset = offset;
	}

	@Override
	public VerificationType getType()
	{
		return VerificationType.ITEM_UNINITIALIZED;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{8, (byte) ((this.offset >>> 8) & 0XFF), (byte) (this.offset & 0XFF)};
	}
}
