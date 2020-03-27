package org.mve.util.asm.file;

public class StructExceptionTable
{
	private short startPc;
	private short endPc;
	private short handlerPc;
	private short catchPc;

	public void setStartPc(short startPc)
	{
		this.startPc = startPc;
	}

	public void setEndPc(short endPc)
	{
		this.endPc = endPc;
	}

	public void setHandlerPc(short handlerPc)
	{
		this.handlerPc = handlerPc;
	}

	public void setCatchPc(short catchPc)
	{
		this.catchPc = catchPc;
	}

	public short getStartPc()
	{
		return startPc;
	}

	public short getEndPc()
	{
		return endPc;
	}

	public short getHandlerPc()
	{
		return handlerPc;
	}

	public short getCatchPc()
	{
		return catchPc;
	}
}
