package org.mve.util.asm;

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
}
