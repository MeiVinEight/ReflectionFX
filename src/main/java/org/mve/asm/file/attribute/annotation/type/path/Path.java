package org.mve.asm.file.attribute.annotation.type.path;

public class Path
{
	public int kind;
	public int argument;

	public byte[] toByteArray()
	{
		return new byte[]{(byte) kind, (byte) argument};
	}
}
