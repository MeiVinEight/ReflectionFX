package org.mve.asm.attribute.code.stack.verification;

import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.stack.verification.VerificationIntegerVariable;

public class IntegerVariable implements Verification
{
	@Override
	public org.mve.asm.file.attribute.stack.verification.Verification transform(ConstantArray pool)
	{
		return new VerificationIntegerVariable();
	}
}
