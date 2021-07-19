package org.mve.asm.attribute.annotation.type.local;

import org.mve.asm.attribute.code.Marker;

public class Variable
{
	public Marker from;
	public Marker to;
	public int slot;

	public Variable(Marker from, Marker to, int slot)
	{
		this.from = from;
		this.to = to;
		this.slot = slot;
	}
}
