package org.mve.asm.attribute.code.stack.verification;

import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.stack.verification.VerificationTopVariable;

public class TopVariable implements Verification
{
	@Override
	public org.mve.asm.file.attribute.stack.verification.Verification transform(ConstantArray pool)
	{
		return new VerificationTopVariable();
	}
}
