package org.mve.asm.file.attribute.stack;

import org.mve.asm.file.attribute.stack.verification.Verification;

import java.util.Arrays;

public class StackMapFullFrame extends StackMapFrame
{
	public int offset;
	public Verification[] local = new Verification[0];
	public Verification[] stack = new Verification[0];

	public void local(Verification verification)
	{
		this.local = Arrays.copyOf(this.local, this.local.length+1);
		this.local[this.local.length-1] = verification;
	}

	public void stack(Verification verification)
	{
		this.stack = Arrays.copyOf(this.stack, this.stack.length+1);
		this.stack[this.stack.length-1] = verification;
	}

	@Override
	public StackMapFrameType type()
	{
		return StackMapFrameType.STACK_MAP_FULL_FRAME;
	}

	@Override
	public int length()
	{
		int len = 7;
		for (Verification v : local) len += v.type().length();
		for (Verification v : stack) len += v.type().length();
		return len;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.length();
		int index = 0;
		byte[] b = new byte[len];
		b[index++] = (byte) this.type;
		b[index++] = (byte) ((this.offset >>> 8) & 0XFF);
		b[index++] = (byte) (this.offset & 0XFF);
		b[index++] = (byte) ((this.local.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.local.length & 0XFF);
		for (Verification v : this.local)
		{
			int l = v.type().length();
			System.arraycopy(v.toByteArray(), 0, b, index, l);
			index+=l;
		}
		b[index++] = (byte) ((this.stack.length >>> 8) & 0XFF);
		b[index++] = (byte) (this.stack.length & 0XFF);
		for (Verification v : this.stack)
		{
			int l = v.type().length();
			System.arraycopy(v.toByteArray(), 0, b, index, l);
			index+=l;
		}
		return b;
	}
}
