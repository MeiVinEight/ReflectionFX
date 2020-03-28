package org.mve.util.asm.file;

public class StackMapSameFrame extends StackMapFrame
{
	public StackMapSameFrame(byte frameType)
	{
		super(frameType);
	}

	@Override
	public StackMapFrameType getType()
	{
		return StackMapFrameType.STACK_MAP_SAME_FRAME;
	}

	@Override
	public int getLength()
	{
		return 1;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{this.getFrameType()};
	}
}
