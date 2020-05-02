package org.mve.util.asm.attribute;

import org.mve.util.asm.ConstantPoolFinder;
import org.mve.util.asm.file.Attribute;
import org.mve.util.asm.file.AttributeSourceFile;
import org.mve.util.asm.file.AttributeType;
import org.mve.util.asm.file.ConstantPool;

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
