package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.Attribute;
import org.mve.asm.file.AttributeSourceFile;
import org.mve.asm.file.AttributeType;
import org.mve.asm.file.ConstantPool;

public class SourceWriter implements AttributeWriter
{
	public final String source;

	public SourceWriter(String source)
	{
		this.source = source;
	}

	@Override
	public Attribute getAttribute(ConstantPool pool)
	{
		AttributeSourceFile attr = new AttributeSourceFile((short) ConstantPoolFinder.findUTF8(pool, AttributeType.SOURCE_FILE.getName()));
		attr.setSourceFileIndex((short) ConstantPoolFinder.findUTF8(pool, this.source));
		return attr;
	}
}
