package org.mve.asm.attribute;

import org.mve.asm.file.Attribute;
import org.mve.asm.file.ConstantPool;

public interface AttributeWriter
{
	Attribute getAttribute(ConstantPool pool);
}
