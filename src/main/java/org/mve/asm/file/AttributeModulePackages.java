package org.mve.asm.file;

public class AttributeModulePackages extends Attribute
{
	private short packageCount;
	private short[] packages = new short[0];

	public AttributeModulePackages(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getPackageCount()
	{
		return packageCount;
	}

	public void addModulePackage(short cpIndex)
	{
		short[] arr = new short[this.packageCount+1];
		System.arraycopy(this.packages, 0, arr, 0, this.packageCount);
		arr[this.packageCount] = cpIndex;
		this.packages = arr;
		this.packageCount++;
	}

	public void setModulePackage(int index, short cpIndex)
	{
		this.packages[index] = cpIndex;
	}

	public short getModulePackage(int index)
	{
		return this.packages[index];
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.MODULE_PACKAGES;
	}

	@Override
	public int getLength()
	{
		return 8 + (2 * this.packageCount);
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		int index = 0;
		b[index++] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[index++] = (byte) (this.getAttributeNameIndex() & 0XFF);
		len -= 6;
		b[index++] = (byte) ((len >>> 24) & 0XFF);
		b[index++] = (byte) ((len >>> 16) & 0XFF);
		b[index++] = (byte) ((len >>> 8) & 0XFF);
		b[index++] = (byte) (len & 0XFF);
		b[index++] = (byte) ((this.packageCount >>> 8) & 0XFF);
		b[index++] = (byte) (this.packageCount & 0XFF);
		for (short s : this.packages)
		{
			b[index++] = (byte) ((s >>> 8) & 0XFF);
			b[index++] = (byte) (s & 0XFF);
		}
		return b;
	}
}
