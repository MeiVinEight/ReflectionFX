package org.mve.util.asm;

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
}
