package org.mve.util.asm.file;

public class ElementValueAnnotation extends ElementValue
{
	private StructAnnotation annotation;

	public ElementValueAnnotation(byte type)
	{
		super(type);
	}

	public StructAnnotation getAnnotation()
	{
		return annotation;
	}

	public void setAnnotation(StructAnnotation annotation)
	{
		this.annotation = annotation;
	}

	@Override
	public int getLength()
	{
		return 1 + this.annotation.getLength();
	}

	@Override
	public byte[] toByteArray()
	{
		int len = this.getLength();
		byte[] b = new byte[len];
		b[0] = this.getType();
		System.arraycopy(this.annotation.toByteArray(), 0, b, 1, len-1);
		return b;
	}
}
