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
		return this.defaultValue.getLength();
	}
}
