package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeDeprecated;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;

public class DeprecatedWriter implements AttributeWriter
{
	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeDeprecated deprecated = new AttributeDeprecated();
		deprecated.name = ConstantPoolFinder.findUTF8(pool, AttributeType.DEPRECATED.getName());
		return deprecated;
	}
}
