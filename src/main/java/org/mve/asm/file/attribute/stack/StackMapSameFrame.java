package org.mve.asm.file.attribute.stack;

public class StackMapSameFrame extends StackMapFrame
{
	@Override
	public StackMapFrameType type()
	{
		return StackMapFrameType.STACK_MAP_SAME_FRAME;
	}

	@Override
	public int length()
	{
		return 1;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) this.type};
	}
}
