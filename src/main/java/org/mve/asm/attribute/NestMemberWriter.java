package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeNestMembers;
import org.mve.asm.file.constant.ConstantArray;

public class NestMemberWriter extends ClassArrayWriter
{
	public NestMemberWriter(String... member)
	{
		super(member);
	}

	public NestMemberWriter()
	{
	}

	public NestMemberWriter member(String name)
	{
		return (NestMemberWriter) this.classes(name);
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeNestMembers attribute = new AttributeNestMembers();
		attribute.name = ConstantPoolFinder.findUTF8(pool, attribute.type().getName());
		attribute.classes = this.array(pool);
		return attribute;
	}
}
