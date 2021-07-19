package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributePermittedSubclasses;
import org.mve.asm.file.constant.ConstantArray;

public class PermittedSubclassWriter extends ClassArrayWriter
{
	public PermittedSubclassWriter(String... member)
	{
		super(member);
	}

	public PermittedSubclassWriter()
	{
		super();
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributePermittedSubclasses attribute = new AttributePermittedSubclasses();
		attribute.name = ConstantPoolFinder.findUTF8(pool, attribute.type().getName());
		attribute.classes = this.array(pool);
		return attribute;
	}
}
