package org.mve.asm.file.constant;

public class ConstantInvokeDynamic extends ConstantDynamic
{
	public ConstantInvokeDynamic(int bootstrap, int nameAndType)
	{
		super(bootstrap, nameAndType);
	}

	@Override
	public ConstantType type()
	{
		return ConstantType.CONSTANT_INVOKE_DYNAMIC;
	}

	@Override
	public byte[] toByteArray()
	{
		byte[] b = super.toByteArray();
		b[0] = this.type().code();
		return b;
	}
}
