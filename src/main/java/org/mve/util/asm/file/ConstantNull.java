package org.mve.util.asm.file;

public class ConstantNull extends ConstantPoolElement
{
	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_NULL;
	}
}
