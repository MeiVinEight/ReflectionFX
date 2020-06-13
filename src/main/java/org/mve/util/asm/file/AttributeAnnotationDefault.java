package org.mve.util.asm.file;

public class AttributeAnnotationDefault extends Attribute
{
	private ElementValue defaultValue;

	public AttributeAnnotationDefault(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public ElementValue getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue(ElementValue defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.ANNOTATION_DEFAULT;
	}

	@Override
	public int getLength()
	{
		return this.defaultValue.getLength() + 6;
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		b[0] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[1] = (byte) (this.getAttributeNameIndex() & 0XFF);
		len -= 6;
		b[2] = (byte) ((len >>> 24) & 0XFF);
		b[3] = (byte) ((len >>> 16) & 0XFF);
		b[4] = (byte) ((len >>> 8) & 0XFF);
		b[5] = (byte) (len & 0XFF);
		System.arraycopy(this.defaultValue.toByteArray(), 0, b, 6, len);
		return b;
	}
}
