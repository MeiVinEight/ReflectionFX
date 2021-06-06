package org.mve.asm.attribute;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.Attribute;
import org.mve.asm.file.AttributeSignature;
import org.mve.asm.file.AttributeType;
import org.mve.asm.file.ConstantPool;

public class SignatureWriter implements AttributeWriter
{
	public final String signature;

	public SignatureWriter(String signature)
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
