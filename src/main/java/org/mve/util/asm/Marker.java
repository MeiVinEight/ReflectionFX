package org.mve.util.asm;

public class Marker
{
	private int addresses = 0;

	public void mark(int addr)
	{
		this.addresses = addr;
	}

	public int get()
	{
		return this.addresses;
	}
}
