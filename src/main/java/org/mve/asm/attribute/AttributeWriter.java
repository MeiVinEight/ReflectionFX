package org.mve.asm.attribute;

import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.constant.ConstantArray;

public interface AttributeWriter
{
	public abstract Attribute getAttribute(ConstantArray pool);
}
