package org.mve.util.asm;

import java.util.Arrays;

public class Marker
{
	private int[] addresses = new int[0];
	private int pointer = 0;

	public void mark(int addr)
	{
		int i = this.addresses.length;
		this.addresses = Arrays.copyOf(this.addresses, i+1);
		this.addresses[i] = addr;
	}

	public int get()
	{
		int addr = this.addresses[this.pointer++];
		if (this.pointer == this.addresses.length) this.pointer = 0;
		return addr;
	}
}
