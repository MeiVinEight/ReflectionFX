package org.mve.util.asm.attribute;

import org.mve.util.asm.ConstantPoolFinder;
import org.mve.util.asm.file.Attribute;
import org.mve.util.asm.file.AttributeSignature;
import org.mve.util.asm.file.AttributeType;
import org.mve.util.asm.file.ConstantPool;

public class AttributeSignatureWriter implements AttributeWriter
{
	public final String signature;

	public AttributeSignatureWriter(String signature)
	{
		this.signature = signature;
	}

	@Override
	public Attribute getAttribute(ConstantPool pool)
	{
		AttributeSignature attr = new AttributeSignature((short) ConstantPoolFinder.findUTF8(pool, AttributeType.SIGNATURE.getName()));
		attr.setSignatureIndex((short) ConstantPoolFinder.findUTF8(pool, signature));
		return attr;
	}
}
