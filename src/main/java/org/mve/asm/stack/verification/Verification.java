package org.mve.asm.stack.verification;

import org.mve.asm.Marker;
import org.mve.asm.file.ConstantPool;

public interface Verification
{
	org.mve.asm.file.Verification transform(ConstantPool pool);

	public static Verification topVariable()
	{
		return new TopVariable();
	}

	public static Verification integerVariable()
	{
		return new IntegerVariable();
	}

	public static Verification floatVariable()
	{
		return new FloatVariable();
	}

	public static Verification nullVariable()
	{
		return new NullVariable();
	}

	public static Verification uninitializedThisVariable()
	{
		return new UninitializedThisVariable();
	}

	public static Verification objectVariable(String type)
	{
		return new ObjectVariable(type);
	}

	public static Verification uninitializedVariable(Marker marker)
	{
		return new UninitializedVariable(marker);
	}

	public static Verification longVariable()
	{
		return new LongVariable();
	}

	public static Verification doubleVariable()
	{
		return new DoubleVariable();
	}
}
