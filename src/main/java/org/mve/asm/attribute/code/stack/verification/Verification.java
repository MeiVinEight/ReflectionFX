package org.mve.asm.attribute.code.stack.verification;

import org.mve.asm.attribute.code.Marker;
import org.mve.asm.file.constant.ConstantArray;

public interface Verification
{
	org.mve.asm.file.attribute.stack.verification.Verification transform(ConstantArray pool);

	static Verification topVariable()
	{
		return new TopVariable();
	}

	static Verification integerVariable()
	{
		return new IntegerVariable();
	}

	static Verification floatVariable()
	{
		return new FloatVariable();
	}

	static Verification nullVariable()
	{
		return new NullVariable();
	}

	static Verification uninitializedThisVariable()
	{
		return new UninitializedThisVariable();
	}

	static Verification objectVariable(String type)
	{
		return new ObjectVariable(type);
	}

	static Verification uninitializedVariable(Marker marker)
	{
		return new UninitializedVariable(marker);
	}

	static Verification longVariable()
	{
		return new LongVariable();
	}

	static Verification doubleVariable()
	{
		return new DoubleVariable();
	}
}
