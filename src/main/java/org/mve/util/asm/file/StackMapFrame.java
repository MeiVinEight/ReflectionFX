package org.mve.util.asm.file;

import org.mve.util.Binary;

public abstract class StackMapFrame implements Binary
{
	private final byte frameType;

	public StackMapFrame(byte frameType)
	{
		this.frameType = frameType;
	}

	public abstract StackMapFrameType getType();

	public abstract int getLength();

	public final byte getFrameType()
	{
		return this.frameType;
	}
}
