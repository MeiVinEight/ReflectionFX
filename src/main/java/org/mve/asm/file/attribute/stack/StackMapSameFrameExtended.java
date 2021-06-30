package org.mve.asm.file.attribute.stack;

public class StackMapSameFrameExtended extends StackMapFrame
{
	public int offset;

	@Override
	public StackMapFrameType type()
	{
		return StackMapFrameType.STACK_MAP_SAME_FRAME_EXTENDED;
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
