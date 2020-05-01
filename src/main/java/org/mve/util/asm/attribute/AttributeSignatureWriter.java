package org.mve.util.asm.attribute;

import org.mve.util.asm.FindableConstantPool;
import org.mve.util.asm.file.Attribute;
import org.mve.util.asm.file.AttributeSignature;
import org.mve.util.asm.file.AttributeType;

public class AttributeSignatureWriter implements AttributeWriter
{
	public final String signature;

	public AttributeSignatureWriter(String signature)
	{
		this.signature = signature;
	}

	@Override
	public Attribute getAttribute(FindableConstantPool pool)
	{
		AttributeSignature attr = new AttributeSignature((short) pool.findUTF8(AttributeType.SIGNATURE.getName()));
		attr.setSignatureIndex((short) pool.findUTF8(signature));
		return attr;
	}
}
