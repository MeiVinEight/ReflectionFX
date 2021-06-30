package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.attribute.Attribute;
import org.mve.asm.file.attribute.AttributeSignature;
import org.mve.asm.file.attribute.AttributeType;
import org.mve.asm.file.constant.ConstantArray;

public class SignatureWriter implements AttributeWriter
{
	public String signature;

	public SignatureWriter(String signature)
	{
		this.signature = signature;
	}

	@Override
	public Attribute getAttribute(ConstantArray pool)
	{
		AttributeSignature attr = new AttributeSignature();
		attr.name = ConstantPoolFinder.findUTF8(pool, AttributeType.SIGNATURE.getName());
		attr.signature = ConstantPoolFinder.findUTF8(pool, signature);
		return attr;
	}
}
