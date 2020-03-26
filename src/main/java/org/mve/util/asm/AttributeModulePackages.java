package org.mve.util.asm;

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
		return 2 + (2 * this.packageCount);
	}
}
