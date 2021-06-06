package org.mve.asm.file;

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
		return 10;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[10];
		int index = 0;
		b[index++] = (byte) ((this.getAttributeNameIndex() >>> 8) & 0XFF);
		b[index++] = (byte) (this.getAttributeNameIndex() & 0XFF);
		b[index++] = 0;
		b[index++] = 0;
		b[index++] = 0;
		b[index++] = 4;
		b[index++] = (byte) ((this.classIndex >>> 8) & 0XFF);
		b[index++] = (byte) (this.classIndex & 0XFF);
		b[index++] = (byte) ((this.methodIndex >>> 8) & 0XFF);
		b[index] = (byte) (this.methodIndex & 0XFF);
		return b;
	}
}
