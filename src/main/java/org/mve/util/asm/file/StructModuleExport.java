package org.mve.util.asm.file;

import org.mve.util.Binary;

public class StructModuleExport implements Binary
{
	private short exportIndex;
	private short exportFlags;
	private short exportToCount;
	private short[] exportTo = new short[0];

	public short getExportIndex()
	{
		return exportIndex;
	}

	public void setExportIndex(short exportIndex)
	{
		this.exportIndex = exportIndex;
	}

	public short getExportFlags()
	{
		return exportFlags;
	}

	public void setExportFlags(short exportFlags)
	{
		this.exportFlags = exportFlags;
	}

	public short getExportToCount()
	{
		return exportToCount;
	}

	public void addExportTo(short cpIndex)
	{
		short[] arr = new short[this.exportToCount+1];
		System.arraycopy(this.exportTo, 0, arr, 0, this.exportToCount);
		arr[this.exportToCount] = cpIndex;
		this.exportTo = arr;
		this.exportToCount++;
	}

	public void setExportTo(int index, short cpIndex)
	{
		this.exportTo[index] = cpIndex;
	}

	public short getExportTo(int index)
	{
		return this.exportTo[index];
	}

	public int getLength()
	{
		return 6 + (2 * this.exportToCount);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		b[0] = (byte) ((this.exportIndex >>> 8) & 0XFF);
		b[1] = (byte) (this.exportIndex & 0XFF);
		b[2] = (byte) ((this.exportFlags >>> 8) & 0XFF);
		b[3] = (byte) (this.exportFlags & 0XFF);
		b[4] = (byte) ((this.exportToCount >>> 8) & 0XFF);
		b[5] = (byte) (this.exportToCount & 0XFF);
		int index = 6;
		for (short s : this.exportTo)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
