package org.mve.util.asm.file;

public class ConstantInvokeDynamic extends ConstantPoolElement
{
	private final short bootstrapMethodAttributeIndex;
	private final short nameAndTypeIndex;

	public ConstantInvokeDynamic(short bootstrapMethodAttributeIndex, short nameAndTypeIndex)
	{
		this.bootstrapMethodAttributeIndex = bootstrapMethodAttributeIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	public short getBootstrapMethodAttributeIndex()
	{
		return bootstrapMethodAttributeIndex;
	}

	public short getNameAndTypeIndex()
	{
		return nameAndTypeIndex;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_INVOKE_DYNAMIC;
	}
}
