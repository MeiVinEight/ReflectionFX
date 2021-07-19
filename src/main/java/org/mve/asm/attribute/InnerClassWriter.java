package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.attribute.inner.InnerClass;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeInnerClasses;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;

import java.util.Arrays;

public class InnerClassWriter implements AttributeWriter
{
	public InnerClass[] inner = new InnerClass[0];

	public InnerClassWriter inner(String inner, String outer, String name, int access)
	{
		return this.inner(new InnerClass(inner, outer, name, access));
	}

	public InnerClassWriter inner(InnerClass inner)
	{
		this.inner = Arrays.copyOf(this.inner, this.inner.length+1);
		this.inner[this.inner.length-1] = inner;
		return this;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeInnerClasses attribute = new AttributeInnerClasses();
		attribute.name = ConstantPoolFinder.findUTF8(pool, AttributeType.INNER_CLASSES.getName());
		for (InnerClass inner : this.inner)
		{
			org.mve.asm.file.attribute.inner.InnerClass ic = new org.mve.asm.file.attribute.inner.InnerClass();
			ic.inner = ConstantPoolFinder.findClass(pool, inner.inner);
			ic.outer = ConstantPoolFinder.findClass(pool, inner.outer);
			ic.name = ConstantPoolFinder.findUTF8(pool, inner.name);
			ic.access = inner.access;
			attribute.inner(ic);
		}
		return attribute;
	}
}
