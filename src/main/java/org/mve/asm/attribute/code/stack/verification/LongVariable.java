package org.mve.asm.attribute.code.stack.verification;

import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.VerificationLongVariable;

public class LongVariable implements Verification
{
	@Override
	public org.mve.asm.file.Verification transform(ConstantPool pool)
	{
		return new VerificationLongVariable();
	}
}
