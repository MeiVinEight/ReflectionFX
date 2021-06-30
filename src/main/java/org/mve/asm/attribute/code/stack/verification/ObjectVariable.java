package org.mve.asm.attribute.code.stack.verification;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.stack.verification.VerificationObjectVariable;

public class ObjectVariable implements Verification
{
	private final String type;

	public ObjectVariable(String type)
	{
		this.type = type;
	}

	@Override
	public org.mve.asm.file.attribute.stack.verification.Verification transform(ConstantArray pool)
	{
		VerificationObjectVariable variable = new VerificationObjectVariable();
		variable.type = (short) ConstantPoolFinder.findClass(pool, this.type);
		return variable;
	}
}
