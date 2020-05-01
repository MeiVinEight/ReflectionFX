package org.mve.util.asm.attribute;

import org.mve.util.asm.FindableConstantPool;
import org.mve.util.asm.file.Attribute;

public interface AttributeWriter
{
	Attribute getAttribute(FindableConstantPool pool);
}
