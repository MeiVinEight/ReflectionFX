package org.mve.asm.stack.verification;

import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.VerificationTopVariable;

public class TopVariable implements Verification
{
	@Override
	public org.mve.asm.file.Verification transform(ConstantPool pool)
	{
		return new VerificationTopVariable();
	}
}
