package org.mve.asm.attribute.code.stack.verification;

import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.stack.verification.VerificationLongVariable;

public class LongVariable implements Verification
{
	@Override
	public org.mve.asm.file.attribute.stack.verification.Verification transform(ConstantArray pool)
	{
		return new VerificationLongVariable();
	}
}
