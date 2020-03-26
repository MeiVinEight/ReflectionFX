package org.mve.util.asm;

public class ConstantMethodHandle extends ConstantPoolElement
{
	private final byte referenceKind;
	private final short referenceIndex;

	public ConstantMethodHandle(byte referenceKind, short referenceIndex)
	{
		this.referenceKind = referenceKind;
		this.referenceIndex = referenceIndex;
	}

	public byte getReferenceKind()
	{
		return referenceKind;
	}

	public short getReferenceIndex()
	{
		return referenceIndex;
	}

	@Override
	public ConstantPoolElementType getType()
	{
		return ConstantPoolElementType.CONSTANT_METHOD_HANDLE;
	}
}
