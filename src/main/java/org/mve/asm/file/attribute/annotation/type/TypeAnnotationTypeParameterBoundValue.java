package org.mve.asm.file.attribute.annotation.type;

public class TypeAnnotationTypeParameterBoundValue extends TypeAnnotationValue
{
	public int parameter;
	public int bound;

	@Override
	public int length()
	{
		return 2;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) this.parameter, (byte) this.bound};
	}
}
