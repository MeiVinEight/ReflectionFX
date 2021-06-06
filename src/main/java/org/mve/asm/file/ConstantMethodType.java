package org.mve.asm.file;

public class ConstantMethodType extends ConstantPoolElement
{
	private final short descriptorIndex;

	public ConstantMethodType(short descriptorIndex)
	{
		this.descriptorIndex = descriptorIndex;
	}

	public short getDescriptorIndex()
	{
		return descriptorIndex;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_METHOD_TYPE;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[3];
		b[0] = this.getType().getCode();
		b[1] = (byte) ((this.descriptorIndex >>> 8) & 0XFF);
		b[2] = (byte) (this.descriptorIndex & 0XFF);
		return b;
	}
}
