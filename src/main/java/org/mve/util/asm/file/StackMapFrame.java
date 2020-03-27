package org.mve.util.asm.file;

public abstract class StackMapFrame
{
	private final byte frameType;

	public StackMapFrame(byte frameType)
	{
		this.frameType = frameType;
	}

	public abstract StackMapFrameType getType();

	public abstract int getLength();

	public byte getFrameType()
	{
		return this.frameType;
	}
}
