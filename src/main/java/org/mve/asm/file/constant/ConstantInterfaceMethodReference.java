package org.mve.asm.file.constant;

public class ConstantInterfaceMethodReference extends ConstantMethodReference
{
	public ConstantInterfaceMethodReference(int classIndex, int nameAndTypeIndex)
	{
		super(classIndex, nameAndTypeIndex);
	}

	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_INTERFACE_METHOD_REFERENCE;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = super.toByteArray();
		b[0] = this.type().code();
		return b;
	}
}
