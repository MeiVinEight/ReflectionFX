package org.mve.asm.file;

public class ConstantNull extends ConstantPoolElement
{
	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_NULL;
	}

	@Override
	public byte[] toByteArray()
	{
		return null;
	}
}
