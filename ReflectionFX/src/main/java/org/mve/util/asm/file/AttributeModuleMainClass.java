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
		return 8;
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
		b[index++] = (byte) ((this.mainClassIndex >>> 8) & 0XFF);
		b[index] = (byte) (this.mainClassIndex & 0XFF);
		return b;
	}
}
