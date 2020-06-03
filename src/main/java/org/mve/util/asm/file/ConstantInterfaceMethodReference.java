package org.mve.util.asm.file;

public class ConstantInterfaceMethodReference extends ConstantMethodReference
{
	public ConstantInterfaceMethodReference(short classIndex, short nameAndTypeIndex)
	{
		super(classIndex, nameAndTypeIndex);
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_INTERFACE_METHOD_REFERENCE;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = super.toByteArray();
		b[0] = this.getType().getCode();
		return b;
	}
}
