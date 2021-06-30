package org.mve.asm.file.constant;

public abstract class Constant
{
	public abstract ConstantType type();

	public abstract byte[] toByteArray();

	public abstract int length();
}
