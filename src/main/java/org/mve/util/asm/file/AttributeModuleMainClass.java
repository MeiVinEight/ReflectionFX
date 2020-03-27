package org.mve.util.asm.file;

public class AttributeModuleMainClass extends Attribute
{
	private short mainClassIndex;

	public AttributeModuleMainClass(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getMainClassIndex()
	{
		return mainClassIndex;
	}

	public void setMainClassIndex(short mainClassIndex)
	{
		this.mainClassIndex = mainClassIndex;
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.MODULE_MAIN_CLASS;
	}

	@Override
	public int getLength()
	{
		return 2;
	}
}
