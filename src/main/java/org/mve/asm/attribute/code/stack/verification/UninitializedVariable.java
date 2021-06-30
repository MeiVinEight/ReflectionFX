package org.mve.asm.attribute.code.stack.verification;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.constant.ConstantArray;
import org.mve.asm.file.attribute.stack.verification.VerificationUninitializedVariable;

public class UninitializedVariable implements Verification
{
	private final Marker offset;

	public UninitializedVariable(Marker offset)
	{
		this.offset = offset;
	}

	@Override
	public org.mve.asm.file.attribute.stack.verification.Verification transform(ConstantArray pool)
	{
		VerificationUninitializedVariable variable = new VerificationUninitializedVariable();
		variable.offset = (short) this.offset.address;
		return variable;
	}
}
