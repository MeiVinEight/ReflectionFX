package org.mve.util.asm.attribute;

import org.mve.util.asm.FindableConstantPool;
import org.mve.util.asm.file.Attribute;
import org.mve.util.asm.file.AttributeSourceFile;
import org.mve.util.asm.file.AttributeType;

public class AttributeSourceWriter implements AttributeWriter
{
	public final String source;

	public AttributeSourceWriter(String source)
	{
		this.source = source;
	}

	@Override
	public Attribute getAttribute(FindableConstantPool pool)
	{
		AttributeSourceFile attr = new AttributeSourceFile((short) pool.findUTF8(AttributeType.SOURCE_FILE.getName()));
		attr.setSourceFileIndex((short) pool.findUTF8(this.source));
		return attr;
	}
}
