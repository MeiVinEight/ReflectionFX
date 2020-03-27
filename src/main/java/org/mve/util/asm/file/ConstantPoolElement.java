package org.mve.util.asm.file;

import org.mve.util.Binary;

public abstract class ConstantPoolElement implements Binary
{
	public abstract ConstantPoolElementType getType();
}
