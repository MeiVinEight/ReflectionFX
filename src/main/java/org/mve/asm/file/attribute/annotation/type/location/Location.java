package org.mve.asm.file.attribute.annotation.type.location;

public class Location
{
	public int kind;
	public int argument;

	public Location(int kind, int argument)
	{
		this.kind = kind;
		this.argument = argument;
	}

	public Location()
	{
	}

	public byte[] toByteArray()
	{
		return new byte[]{(byte) kind, (byte) argument};
	}
}
