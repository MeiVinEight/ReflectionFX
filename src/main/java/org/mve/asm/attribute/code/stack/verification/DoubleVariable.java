package org.mve.asm.attribute.code.stack.verification;

import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.VerificationDoubleVariable;

public class DoubleVariable implements Verification
{
	@Override
	public org.mve.asm.file.Verification transform(ConstantPool pool)
	{
		return new VerificationDoubleVariable();
	}
}
