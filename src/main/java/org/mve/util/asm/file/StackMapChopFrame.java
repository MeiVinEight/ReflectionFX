package org.mve.util.asm.file;

public class StackMapChopFrame extends StackMapFrame
{
	private short offsetDelta;

	public StackMapChopFrame(byte frameType)
	{
		super(frameType);
	}

	public short getOffsetDelta()
	{
		return offsetDelta;
	}

	public void setOffsetDelta(short offsetDelta)
	{
		this.offsetDelta = offsetDelta;
	}

	@Override
	public StackMapFrameType getType()
	{
		return StackMapFrameType.STACK_MAP_CHOP_FRAME;
	}

	@Override
	public int getLength()
	{
		return 3;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{this.getFrameType(), (byte) ((this.offsetDelta >>> 8) & 0XFF), (byte) (this.offsetDelta & 0XFF)};
	}
}
