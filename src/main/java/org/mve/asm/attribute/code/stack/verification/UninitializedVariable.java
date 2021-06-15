package org.mve.asm.attribute.code.stack.verification;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.ConstantPool;
import org.mve.asm.file.VerificationUninitializedVariable;

public class UninitializedVariable implements Verification
{
	private final Marker offset;

	public UninitializedVariable(Marker offset)
	{
		this.offset = offset;
	}

	@Override
	public org.mve.asm.file.Verification transform(ConstantPool pool)
	{
		VerificationUninitializedVariable variable = new VerificationUninitializedVariable();
		variable.setOffset((short) this.offset.address);
		return variable;
	}
}
