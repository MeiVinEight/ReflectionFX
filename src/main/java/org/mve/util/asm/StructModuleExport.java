package org.mve.util.asm;

public class StructModuleExport
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
}
