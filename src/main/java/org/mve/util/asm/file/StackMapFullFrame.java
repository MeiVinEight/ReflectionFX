package org.mve.util.asm.file;

public class StackMapFullFrame extends StackMapFrame
{
	private short offsetDelta;
	private short localCount;
	private Verification[] locals = new Verification[0];
	private short stackItemCount;
	private Verification[] stackItems = new Verification[0];

	public StackMapFullFrame(byte frameType)
	{
		super(frameType);
	}

	public short getOffsetDelta()
	{
		return offsetDelta;
	}

	public void setOffsetDelta(short offsetDelta)
	{
		this.offsetDelta = offsetDelta;
	}

	public short getLocalCount()
	{
		return localCount;
	}

	public short getStackItemCount()
	{
		return stackItemCount;
	}

	public void addLocal(Verification verification)
	{
		Verification[] locals = new Verification[++this.localCount];
		System.arraycopy(this.locals, 0, locals, 0, this.locals.length);
		locals[this.locals.length] = verification;
		this.locals = locals;
	}

	public void setLocal(int index, Verification verification)
	{
		this.locals[index] = verification;
	}

	public Verification getLocal(int index)
	{
		return this.locals[index];
	}

	public void addStackItem(Verification verification)
	{
		Verification[] stackItems = new Verification[++this.stackItemCount];
		System.arraycopy(this.stackItems, 0, stackItems, 0, this.stackItems.length);
		stackItems[this.stackItems.length] = verification;
		this.stackItems = stackItems;
	}

	public void setStackItem(int index, Verification verification)
	{
		this.stackItems[index] = verification;
	}

	public Verification getStackItem(int idnex)
	{
		return this.stackItems[idnex];
	}

	@Override
	public StackMapFrameType getType()
	{
		return StackMapFrameType.STACK_MAP_FULL_FRAME;
	}

	@Override
	public int getLength()
	{
		int len = 7;
		for (Verification v : locals) len += v.getType().getLength();
		for (Verification v : stackItems) len += v.getType().getLength();
		return len;
	}
}
