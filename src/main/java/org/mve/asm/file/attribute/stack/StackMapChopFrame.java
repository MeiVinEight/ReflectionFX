package org.mve.asm.file.attribute.stack;

public class StackMapChopFrame extends StackMapFrame
{
	public int offset;

	@Override
	public StackMapFrameType type()
	{
		return StackMapFrameType.STACK_MAP_CHOP_FRAME;
	}

	@Override
	public int length()
	{
		return 3;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) this.type, (byte) ((this.offset >>> 8) & 0XFF), (byte) (this.offset & 0XFF)};
	}
}
