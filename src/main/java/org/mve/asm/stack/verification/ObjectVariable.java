package org.mve.asm.stack.verification;

import org.mve.asm.ConstantPoolFinder;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.VerificationObjectVariable;

public class ObjectVariable implements Verification
{
	private final String type;

	public ObjectVariable(String type)
	{
		this.type = type;
	}

	@Override
	public org.mve.asm.file.Verification transform(ConstantPool pool)
	{
		VerificationObjectVariable variable = new VerificationObjectVariable();
		variable.setConstantPoolIndex((short) ConstantPoolFinder.findClass(pool, this.type));
		return variable;
	}
}
