package org.mve.asm.file;

import org.mve.util.Binary;

public class StructExceptionTable implements Binary
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

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[8];
		b[0] = (byte) ((this.startPc >>> 8) & 0XFF);
		b[1] = (byte) (this.startPc & 0XFF);
		b[2] = (byte) ((this.endPc >>> 8) & 0XFF);
		b[3] = (byte) (this.endPc & 0XFF);
		b[4] = (byte) ((this.handlerPc >>> 8) & 0XFF);
		b[5] = (byte) (this.handlerPc & 0XFF);
		b[6] = (byte) ((this.catchPc >>> 8) & 0XFF);
		b[7] = (byte) (this.catchPc & 0XFF);
		return b;
	}
}
