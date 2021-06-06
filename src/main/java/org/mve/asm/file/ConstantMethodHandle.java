package org.mve.asm.file;

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

	@Override
	public byte[] toByteArray()
	{
		byte[] b = new byte[4];
		b[0] = this.getType().getCode();
		b[1] = this.referenceKind;
		b[2] = (byte) ((this.referenceIndex >>> 8) & 0XFF);
		b[3] = (byte) (this.referenceIndex & 0XFF);
		return b;
	}
}
