package org.mve.util.asm.attribute;

import org.mve.util.asm.file.Attribute;
import org.mve.util.asm.file.ConstantPool;

public interface AttributeWriter
{
	Attribute getAttribute(ConstantPool pool);
}
