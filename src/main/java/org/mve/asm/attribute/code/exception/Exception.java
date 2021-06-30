package org.mve.asm.attribute.code.exception;

import org.mve.asm.attribute.code.Marker;

public class Exception
{
	public Marker start;
	public Marker end;
	public Marker caught;
	public String type;

	public Exception(Marker start, Marker end, Marker caught, String type)
	{
		this.start = start;
		this.end = end;
		this.caught = caught;
		this.type = type;
	}
}
