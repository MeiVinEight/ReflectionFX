package org.mve.util.asm.file;

public class ConstantInvokeDynamic extends ConstantDynamic
{
	public ConstantInvokeDynamic(short bootstrapMethodAttributeIndex, short nameAndTypeIndex)
	{
		super(bootstrapMethodAttributeIndex, nameAndTypeIndex);
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_INVOKE_DYNAMIC;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = super.toByteArray();
		b[0] = this.getType().getCode();
		return b;
	}
}
