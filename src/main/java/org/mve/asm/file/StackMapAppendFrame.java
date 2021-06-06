package org.mve.asm.file;

public class StackMapAppendFrame extends StackMapFrame
{
	private short offsetDelta;
	private Verification[] verifications;

	public StackMapAppendFrame(byte frameType)
	{
		super(frameType);
		this.verifications = new Verification[(frameType & 0XFF) - 251];
	}

	public short getOffsetDelta()
	{
		return offsetDelta;
	}

	public void setOffsetDelta(short offsetDelta)
	{
		this.offsetDelta = offsetDelta;
	}

	public void setVerification(int index, Verification verification)
	{
		this.verifications[index] = verification;
	}

	public Verification getVerification(int index)
	{
		return this.verifications[index];
	}

	@Override
	public StackMapFrameType getType()
	{
		return StackMapFrameType.STACK_MAP_APPEND_FRAME;
	}

	@Override
	public int getLength()
	{
		int len = 3;
		for (Verification verification : verifications) len += verification.getType().getLength();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = this.getFrameType();
		b[index++] = (byte) ((this.offsetDelta >>> 8) & 0XFF);
		b[index++] = (byte) (this.offsetDelta & 0XFF);
		for (Verification v : this.verifications)
		{
			int l = v.getType().getLength();
			System.arraycopy(v.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
