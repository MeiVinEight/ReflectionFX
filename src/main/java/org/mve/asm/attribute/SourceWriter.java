package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeSourceFile;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;

public class SourceWriter implements AttributeWriter
{
	public String source;

	public SourceWriter(String source)
	{
		this.source = source;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeSourceFile attr = new AttributeSourceFile();
		attr.name = ConstantPoolFinder.findUTF8(pool, AttributeType.SOURCE_FILE.getName());
		attr.source = ConstantPoolFinder.findUTF8(pool, this.source);
		return attr;
	}
}
