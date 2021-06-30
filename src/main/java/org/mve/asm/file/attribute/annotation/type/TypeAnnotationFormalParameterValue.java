package org.mve.asm.file.attribute.annotation.type;

public class TypeAnnotationFormalParameterValue extends TypeAnnotationValue
{
	public int parameter;

	@Override
	public int length()
	{
		return 1;
	}

	@Override
	public byte[] toByteArray()
	{
		return new byte[]{(byte) this.parameter};
	}
}
