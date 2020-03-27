package org.mve.util.asm.file;

public class AttributeEnclosingMethod extends Attribute
{
	private short classIndex;
	private short methodIndex;

	public AttributeEnclosingMethod(short attributeNameIndex)
	{
		super(attributeNameIndex);
	}

	public short getClassIndex()
	{
		return classIndex;
	}

	public void setClassIndex(short classIndex)
	{
		this.classIndex = classIndex;
	}

	public short getMethodIndex()
	{
		return methodIndex;
	}

	public void setMethodIndex(short methodIndex)
	{
		this.methodIndex = methodIndex;
	}

	@Override
	public AttributeType getType()
	{
		return AttributeType.ENCLOSING_METHOD;
	}

	@Override
	public int getLength()
	{
		return 4;
	}
}
