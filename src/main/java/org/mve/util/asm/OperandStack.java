package org.mve.util.asm;

public class OperandStack
{
	private int max;
	private int used;

	public void push()
	{
		used++;
		max = Math.max(used, max);
	}

	public void pop()
	{
		used--;
	}

	public int getMaxSize()
	{
		return max;
	}
}
