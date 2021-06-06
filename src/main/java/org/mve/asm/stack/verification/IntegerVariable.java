package org.mve.asm.stack.verification;

import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.VerificationIntegerVariable;

public class IntegerVariable implements Verification
{
	@Override
	public org.mve.asm.file.Verification transform(ConstantPool pool)
	{
		return new VerificationIntegerVariable();
	}
}
