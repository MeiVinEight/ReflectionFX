package org.mve.util.asm.file;

public class AttributeConstantValue extends Attribute
{
	/**
	 * The value of the valueIndex item must be
	 * a valid index into the constant_pool table.
	 * The constant_pool entry at that index gives
	 * the constant value represented by this attribute.
	 * The constant_pool entry must be of a type
	 * appropriate to the field, as specified in
	 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.2-300-C.1">Table 4.7.2-A.<a/>
	 */
	private short valueIndex;

	public AttributeConstantValue(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getValueIndex()
	{
		return valueIndex;
	}

	public void setValueIndex(short valueIndex)
	{
		this.valueIndex = valueIndex;
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.CONSTANT_VALUE;
	}

	@Override
	public int getLength()
	{
		return 2;
	}
}
